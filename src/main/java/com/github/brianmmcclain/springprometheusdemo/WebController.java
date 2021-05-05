package com.github.brianmmcclain.springprometheusdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
public class WebController {

    Counter visitCounter;
    Timer visitTimer;
    Gauge visitGauge;

    List<String> list = new ArrayList<>();


    public WebController(MeterRegistry registry) {
        visitCounter = Counter.builder("visit_counter")
            .description("Number of visits to the site")
            .register(registry);

        visitTimer =  Timer.builder("visit_timer")
            .description("Time of visits to the site")
            .register(registry);

        visitGauge = Gauge.builder("visit_gauge", list, List::size)
            .description("Gauge of visits to the site")
            .register(registry);
    }

    @GetMapping("/")
    public String index() {
        visitCounter.increment();
        return "Hello World!";
    }

    @GetMapping("/visits")
    public double visitCount() {
        return visitCounter.count();
    }

    @GetMapping("/visit_time")
    public double visitTime() {
        visitTimer.record(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1500);
            } catch (InterruptedException ignored) { }
        });
        return visitTimer.totalTime(TimeUnit.MILLISECONDS);
    }

    @GetMapping("/visit_gauge")
    public double visitGauge() {
        LocalTime time = LocalTime.now(); // Gets the current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        list.add(time.format(formatter));

        return visitGauge.value();
    }


    
}