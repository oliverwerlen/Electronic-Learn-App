package ch.hslu.springbootbackend.springbootbackend.controllers;

import ch.hslu.springbootbackend.springbootbackend.Entity.Statistic;
import ch.hslu.springbootbackend.springbootbackend.DTO.StatisticDTO;
import ch.hslu.springbootbackend.springbootbackend.Exception.ResourceNotFoundException;
import ch.hslu.springbootbackend.springbootbackend.Service.EntityService.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {
    private final Logger LOG = LoggerFactory.getLogger(StatisticController.class);


    @Autowired
    private StatisticService statisticService;

    @GetMapping("/User/{id}")
    //@PreAuthorize("hasRole('ROLE_USER')")
    public List<Statistic> getStatisticByUserid(@PathVariable(value = "id") Integer userId) throws ResourceNotFoundException {
            List<Statistic> foundStatistic = statisticService.getByUserId(userId);
            return foundStatistic;
    }

    @GetMapping("/UserAndQuestion/")
    //@PreAuthorize("hasRole('ROLE_USER')")
    public List<Statistic> getStatisticByUserAndQuestion(@RequestParam Integer userId
                                                        , @RequestParam Integer questionId) throws ResourceNotFoundException {
        List<Statistic> foundStatistic = statisticService.getByUserAndQuestion(userId, questionId);
        return foundStatistic;
    }

    @GetMapping("/Question/{id}")
    //will be only available for teacher
    //@PreAuthorize("hasRole('ROLE_USER')")
    public List<Statistic> getStatisticByQuestionId(@PathVariable(value = "id") Integer questionId) throws ResourceNotFoundException {
            List<Statistic> foundStatistic = statisticService.getByQuestionId(questionId);
            return foundStatistic;
    }

    @PostMapping("")
    //@PreAuthorize("")
    public Statistic addStatistic(@RequestBody StatisticDTO newStatistic) {
        LOG.warn(newStatistic.toString());
        return statisticService.addNewStatistic(newStatistic);
    }
}