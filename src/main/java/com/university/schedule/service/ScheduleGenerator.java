package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.DayScheduleItem;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.utility.ScheduleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleGenerator {

    @Autowired
    private ScheduleValidator scheduleValidator;

    @Autowired
    private ScheduledClassService scheduledClassService;

    public void generate(LocalDate startDate, LocalDate endTime, List<DayScheduleItem> dayScheduleItems){
        if (!scheduleValidator.isValid(startDate, endTime, dayScheduleItems)){
            throw new ServiceException("There is conflict in dayScheduleItems or start/end Date");
        }

        ScheduledClass scheduledClass;
        try {
            for (DayScheduleItem dayScheduleItem : dayScheduleItems){

                LocalDate date = startDate;
                while (date.isBefore(endTime)){
                    if (date.getDayOfWeek() == dayScheduleItem.getDayOfWeek()){
                        scheduledClass = ScheduledClass.builder()
                                .groups(dayScheduleItem.getGroups())
                                .classroom(dayScheduleItem.getClassroom())
                                .teacher(dayScheduleItem.getTeacher())
                                .course(dayScheduleItem.getCourse())
                                .classType(dayScheduleItem.getClassType())
                                .classTime(dayScheduleItem.getClassTime())
                                .date(date)
                                .build();
                        scheduledClassService.save(scheduledClass);
                        date = date.plusWeeks(1);
                        continue;
                    }
                    date = date.plusDays(1);
                }
            }
        } catch (DataAccessException e){
            throw new ServiceException("Can`t generate schedule");
        }

    }
}
