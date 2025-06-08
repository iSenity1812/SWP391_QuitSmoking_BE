package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Integer id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Integer id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setMember(appointmentDetails.getMember());
            appointment.setCoach(appointmentDetails.getCoach());
            appointment.setStartTime(appointmentDetails.getStartTime());
            appointment.setEndTime(appointmentDetails.getEndTime());
            appointment.setStatus(appointmentDetails.getStatus());
            appointment.setNote(appointmentDetails.getNote());
            return appointmentRepository.save(appointment);
        }).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public void deleteAppointment(Integer id) {
        appointmentRepository.deleteById(id);
    }
}