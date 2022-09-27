package com.elogist.vehicle_master_and_alert_creation.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParseStat {

    private Long parsedMessages;
    private Long messagesSentToRegularKafka;
    private Long messagesSentToHistoryKafka;
}
