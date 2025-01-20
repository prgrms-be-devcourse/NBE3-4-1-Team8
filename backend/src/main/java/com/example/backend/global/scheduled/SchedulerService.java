package com.example.backend.global.scheduled;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.orders.repository.OrdersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
	private final OrdersRepository ordersRepository;

	@Transactional
	@Scheduled(cron = "0 0 14 * * ?")
	public void scheduleOrderProcessing() {
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime startTime = now.minusDays(1).with(LocalTime.of(14, 0));
		ZonedDateTime endTime = now.with(LocalTime.of(14, 0));

		ordersRepository.bulkUpdateDeliveryStatus(startTime, endTime);
	}

}
