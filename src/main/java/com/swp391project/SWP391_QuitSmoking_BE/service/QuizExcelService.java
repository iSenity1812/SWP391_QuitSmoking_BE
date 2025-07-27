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
    public static String SHEET = "Quizzes";
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Quiz> excelToQuizzes(InputStream is, UUID createdByUserId, UserRepository userRepository) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Quiz> quizzes = new ArrayList<Quiz>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Quiz quiz = new Quiz();
                Set<Option> options = new HashSet<>();
                User creator = userRepository.findById(createdByUserId)
                        .orElseThrow(() -> new ResourceNotFoundException("User creating Quiz not found with ID: " + createdByUserId));
                quiz.setCreatedByAdmin(creator);
                quiz.setCreatedAt(LocalDateTime.now());
                quiz.setUpdatedAt(LocalDateTime.now());

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            quiz.setTitle(currentCell.getStringCellValue());
                            break;

                        case 1:
                            quiz.setDescription(currentCell.getStringCellValue());
                            break;

                        case 2:
                        case 4:
                        case 6:
                        case 8:
                            Option option = new Option();
                            option.setContent(currentCell.getStringCellValue());
                            option.setQuiz(quiz);
                            option.setCreatedAt(LocalDateTime.now());
                            option.setUpdatedAt(LocalDateTime.now());
                            option.setCreatedByAdmin(creator);
                            options.add(option);
                            break;

                        case 3:
                        case 5:
                        case 7:
                        case 9:
                            ((Option) options.toArray()[options.size() - 1]).setIsCorrect(currentCell.getBooleanCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                quiz.setOptions(options);

                quizzes.add(quiz);
            }

            workbook.close();

            return quizzes;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
