package com.swp391project.SWP391_QuitSmoking_BE.service;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Option;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Quiz;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

public class QuizExcelService {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String[] HEADERs = { "Title", "Description", "Option1", "isCorrect1", "Option2", "isCorrect2", "Option3", "isCorrect3", "Option4", "isCorrect4" };
    
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Quiz> excelToQuizzes(InputStream is, UUID createdByUserId, UserRepository userRepository) {
        System.out.println("userid: " + createdByUserId);
        try {
            Workbook workbook = new XSSFWorkbook(is);

            // Lấy sheet đầu tiên thay vì tìm sheet có tên cụ thể
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("No sheet found in the Excel file");
            }
            Iterator<Row> rows = sheet.iterator();

            List<Quiz> quizzes = new ArrayList<Quiz>();
            
            // Tìm user một lần duy nhất
            System.out.println("Finding user with ID: " + createdByUserId);
            User creator = userRepository.findById(createdByUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User creating Quiz not found with ID: " + createdByUserId));
            System.out.println("Found user: " + creator.getUsername() + " with role: " + creator.getRole());

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                
                try {
                    System.out.println("Processing row " + rowNumber);
                    Quiz quiz = processRow(currentRow, creator);
                    if (quiz != null) {
                        quizzes.add(quiz);
                        System.out.println("Successfully processed quiz: " + quiz.getTitle());
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row " + rowNumber + ": " + e.getMessage());
                    throw new RuntimeException("Error processing row " + rowNumber + ": " + e.getMessage(), e);
                }
                
                rowNumber++;
            }

            workbook.close();
            System.out.println("Successfully processed " + quizzes.size() + " quizzes");
            return quizzes;
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception in excelToQuizzes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
    
    private static Quiz processRow(Row currentRow, User creator) {
        Iterator<Cell> cellsInRow = currentRow.iterator();
        
        Quiz quiz = new Quiz();
        Set<Option> options = new HashSet<>();
        quiz.setCreatedByAdmin(creator);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        // Lưu tạm thời các option để có thể set isCorrect sau
        List<Option> tempOptions = new ArrayList<>();
        
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();

            try {
                switch (cellIdx) {
                    case 0:
                        quiz.setTitle(currentCell.getStringCellValue());
                        break;

                    case 1:
                        quiz.setDescription(currentCell.getStringCellValue());
                        break;

                    case 2: // Option1
                    case 4: // Option2
                    case 6: // Option3
                    case 8: // Option4
                        Option option = new Option();
                        option.setContent(currentCell.getStringCellValue());
                        option.setQuiz(quiz);
                        option.setCreatedAt(LocalDateTime.now());
                        option.setUpdatedAt(LocalDateTime.now());
                        option.setCreatedByAdmin(creator);
                        tempOptions.add(option);
                        break;

                    case 3: // isCorrect1
                    case 5: // isCorrect2
                    case 7: // isCorrect3
                    case 9: // isCorrect4
                        int optionIndex = (cellIdx - 3) / 2;
                        if (optionIndex < tempOptions.size()) {
                            tempOptions.get(optionIndex).setIsCorrect(currentCell.getBooleanCellValue());
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error processing cell " + cellIdx + ": " + e.getMessage());
                // Try to get string value if boolean fails
                if (cellIdx == 3 || cellIdx == 5 || cellIdx == 7 || cellIdx == 9) {
                    try {
                        String boolStr = currentCell.getStringCellValue().toLowerCase();
                        boolean isCorrect = "true".equals(boolStr) || "1".equals(boolStr) || "yes".equals(boolStr);
                        int optionIndex = (cellIdx - 3) / 2;
                        if (optionIndex < tempOptions.size()) {
                            tempOptions.get(optionIndex).setIsCorrect(isCorrect);
                        }
                    } catch (Exception ex) {
                        System.err.println("Failed to parse boolean value, defaulting to false");
                        int optionIndex = (cellIdx - 3) / 2;
                        if (optionIndex < tempOptions.size()) {
                            tempOptions.get(optionIndex).setIsCorrect(false);
                        }
                    }
                }
            }

            cellIdx++;
        }
        
        // Thêm tất cả options từ tempOptions vào HashSet
        options.addAll(tempOptions);
        quiz.setOptions(options);

        return quiz;
    }

}
