package com.elogist.vehicle_master_and_alert_creation.services;

import com.elogist.vehicle_master_and_alert_creation.clients.DynamicRouteClient;
import com.elogist.vehicle_master_and_alert_creation.clients.RouteClient;
import com.elogist.vehicle_master_and_alert_creation.kafka.AlertKafkaProducer;
import com.elogist.vehicle_master_and_alert_creation.models.*;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert.SimpleAlert;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationALertDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.DeviationParameterDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.Issues;
import com.elogist.vehicle_master_and_alert_creation.models.dto.JsonResponse;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.FoEscalationTicketsRepository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp1Repository;
//import com.elogist.vehicle_master_and_alert_creation.repository.pstgresql.MasterTableTemp2Repository;
import com.elogist.vehicle_master_and_alert_creation.repository.postgresql.MasterTableTemp2Repository;

import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.elogist.vehicle_master_and_alert_creation.utils.StringToClass;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.timgroup.statsd.StatsDClient;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swift.common.pojos.dto.MasterDynamicRouteTemp12;
import swift.common.pojos.dto.MasterDynamicRouteTemp2;
import swift.common.pojos.dto.MasterRouteTempR1R2DTO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AlertProcessingService {

    @Autowired
    MasterTableTemp1Repository masterTableTemp1Repository;

    @Autowired
    MasterTableTemp2Repository masterTableTemp2Repository;

    @Autowired
    IssueBenchmarkService issueBenchmarkService;

    @Autowired
    FoEscalationTicketsRepository foEscalationTicketsRepository;

    @Autowired
    MedAlertProcessingService medAlertProcessingService;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    AlertResolveService alertResolveService;

    @Autowired
    AlertUtilityService alertUtilityService;

    @Autowired
    AlertKafkaProducer alertKafkaProducer;

    @Autowired
    @Lazy
    AsyncService asyncService;

    @Autowired
    StatsDClient dataDogClient;

    @Autowired
    RouteClient routeClient;

    @Autowired
    DynamicRouteClient dynamicRouteClient;

    @Autowired
    M1M2SequenceService m1M2SequenceService;

    @Value("${alertKafka.topic}")
    private String kafkaAlertTopic;


    public static final Logger LOGGER = LoggerFactory.getLogger(AlertProcessingService.class);

    public static Map<Integer, Integer> alertsMap = new HashMap<>();

    @Scheduled(cron = "${elogist.alertProcessing}")
    public List<FoEscalationTickets> getM1M2SimpleAlert() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        LOGGER.info("Started getM1M2SimpleAlert " + LocalDateTime.now());
        List<FoEscalationTickets> foEscalationTickets = null;
        try {

            Boolean requiredM1 = true, requiredM2 = false;

            Long actualTimeInSec = 0l;

            //Getting common entries from MasterTableTemp1 and MasterTableTemp2
            Map<Integer, MasterTableTemp1> M1M2Map = getM1M2Intersection();

            LocalDateTime simpleAlertStartTime = LocalDateTime.now();

            LOGGER.info("M1M2 Fetched & Started Simple Generation getM1M2SimpleAlert " + LocalDateTime.now());

            //Driver code for getting Simple Alerts
            List<FoEscalationTickets> foEscalationTicketsList1 = getSimpleAlert(M1M2Map, requiredM1, requiredM2);

            LocalDateTime simpleAlertEndTime = LocalDateTime.now();

            actualTimeInSec = DateAndTime.getSecDifference(simpleAlertStartTime, simpleAlertEndTime);

            dataDogClient.time("M1M2SimpleAlert", actualTimeInSec, "");

            LOGGER.info("Ended Simple Generation & Started Medium Generation getM1M2SimpleAlert " + LocalDateTime.now());

            List<FoEscalationTickets> foEscalationTicketsList2 = medAlertProcessingService.getMedProcessingAlert(M1M2Map, requiredM1);

            LocalDateTime mediumAlertEndTime = LocalDateTime.now();

            actualTimeInSec = DateAndTime.getSecDifference(simpleAlertEndTime, mediumAlertEndTime);

            dataDogClient.time("M1M2MediumAlert", actualTimeInSec, "");

            LOGGER.info("Ended Medium Generation getM1M2SimpleAlert " + LocalDateTime.now());

            foEscalationTickets = Stream.concat(foEscalationTicketsList1.stream(), foEscalationTicketsList2.stream()).collect(Collectors.toList());

//            saveIntoFoEscalation(foEscalationTickets);

        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        LOGGER.info("Ended getM1M2SimpleAlert " + LocalDateTime.now());
        return foEscalationTickets;
    }

    public Map<Integer, MasterTableTemp1> getM1M2Intersection() {

        List<MasterRouteTempR1R2DTO> masterRouteTempR1R2DTOS = getCombinedRouteR1R2();

        List<MasterDynamicRouteTemp12> masterDynamicDynamicRouteTemp12List = getCombinedDynamicRouteR1R2();

        List<MasterTableTemp1> master1 = new ArrayList<>();
        List<MasterTableTemp2> master2 = new ArrayList<>();
        List<Timestamp> localDateTimeList = masterTableTemp1Repository.getCreatedTime();
        String profile = "";
        String[] activeProfiles = environment.getActiveProfiles();      // it will return String Array of all active profile.
        for (String environmentProfile : activeProfiles) {
            profile = environmentProfile;
        }
        if (profile.startsWith("local")) {
            master1 = List.of(masterTableTemp1Repository.findById(165889).orElse(null),masterTableTemp1Repository.findById(165913).orElse(null));
            master2 = List.of(masterTableTemp2Repository.findById(165889).orElse(null),masterTableTemp2Repository.findById(165913).orElse(null));
        } else {
            master1 = masterTableTemp1Repository.findAll();
            master2 = masterTableTemp2Repository.findAll();
        }
        Map<Integer, MasterTableTemp1> m1Map = new HashMap<>();
        Map<Integer, MasterTableTemp1> resultantMap = new HashMap<>();

        for (MasterTableTemp1 masterTableTemp1 : master1) {

            masterTableTemp1.setCreatedTime(localDateTimeList.get(0).toLocalDateTime());
            m1Map.put(masterTableTemp1.getVId(), masterTableTemp1);

        }
        for (MasterTableTemp2 masterTableTemp2 : master2) {

            masterTableTemp2.setCreatedTime(localDateTimeList.get(1).toLocalDateTime());

            if (m1Map.containsKey(masterTableTemp2.getVId())) {


                m1Map.get(masterTableTemp2.getVId()).setMasterTableTemp2(masterTableTemp2);
                resultantMap.put(masterTableTemp2.getVId(), m1Map.get(masterTableTemp2.getVId()));
            }
        }

        if(masterRouteTempR1R2DTOS.size() > 0) {

            for (MasterRouteTempR1R2DTO masterRouteTempR1R2DTO : masterRouteTempR1R2DTOS) {

                if (m1Map.containsKey(masterRouteTempR1R2DTO.getVehicleId())) {

                    m1Map.get(masterRouteTempR1R2DTO.getVehicleId()).getMasterTableTemp2().setMasterRouteTemp2DTO(masterRouteTempR1R2DTO.getMasterRouteTemp2());
                    m1Map.get(masterRouteTempR1R2DTO.getVehicleId()).setMasterRouteTempR1R2DTO(masterRouteTempR1R2DTO);
                    resultantMap.put(masterRouteTempR1R2DTO.getVehicleId(), m1Map.get(masterRouteTempR1R2DTO.getVehicleId()));

                }
            }
        }

        if(masterDynamicDynamicRouteTemp12List.size() > 0){

            for(MasterDynamicRouteTemp12 masterDynamicDynamicRouteTemp12 : masterDynamicDynamicRouteTemp12List){

                if(m1Map.containsKey(masterDynamicDynamicRouteTemp12.getVehicleId())){

                    m1Map.get(masterDynamicDynamicRouteTemp12.getVehicleId()).getMasterTableTemp2().setMasterDynamicDynamicRouteTemp2(masterDynamicDynamicRouteTemp12.getMasterDynamicRouteTemp2());
                    m1Map.get(masterDynamicDynamicRouteTemp12.getVehicleId()).setMasterDynamicDynamicRouteTemp12(masterDynamicDynamicRouteTemp12);
                    resultantMap.put(masterDynamicDynamicRouteTemp12.getVehicleId(), m1Map.get(masterDynamicDynamicRouteTemp12.getVehicleId()));
                }
            }
        }

        return resultantMap;
    }

    public List<MasterRouteTempR1R2DTO> getCombinedRouteR1R2(){

        Gson gson = new Gson();

        List<MasterRouteTempR1R2DTO> masterRouteTempR1R2DTOList = new ArrayList<>();

        try{

        JsonResponse jsonResponse = routeClient.getRouteData();
        Type type1 = new TypeToken<List<MasterRouteTempR1R2DTO>>() {}.getType();

        String response = gson.toJson(jsonResponse.getData());

        masterRouteTempR1R2DTOList = gson.fromJson(response, type1);

        return masterRouteTempR1R2DTOList;

        }
        catch (Exception e){
            LOGGER.error("Routes Feign Exception---->" + e.getMessage());
        }

        return masterRouteTempR1R2DTOList;

    }

    public List<MasterDynamicRouteTemp12> getCombinedDynamicRouteR1R2(){

        Gson gson = new Gson();

        List<MasterDynamicRouteTemp12> masterDynamicDynamicRouteTemp12s = new ArrayList<>();

        try{

            JsonResponse jsonResponse = dynamicRouteClient.getDynamicRouteData();
            Type type1 = new TypeToken<List<MasterDynamicRouteTemp12>>() {}.getType();

            String response = gson.toJson(jsonResponse.getData());

            masterDynamicDynamicRouteTemp12s = gson.fromJson(response, type1);

            return masterDynamicDynamicRouteTemp12s;

        }
        catch (Exception e){
            LOGGER.error("Routes Feign Exception---->" + e.getMessage());
        }

        return masterDynamicDynamicRouteTemp12s;

    }

    public List<Class<? extends SimpleAlert>> getSimpleAlertClassesList() {
        Reflections reflections = new Reflections("com.elogist.vehicle_master_and_alert_creation.models", new SubTypesScanner(false));
        Set<Class<? extends SimpleAlert>> classes = reflections.getSubTypesOf(SimpleAlert.class);
        List<Class<? extends SimpleAlert>> simpleAlertList = new ArrayList<>();
        for (Class<? extends SimpleAlert> clas : classes) {
            simpleAlertList.add(clas);
        }
        return simpleAlertList;
    }

    public List<FoEscalationTickets> getSimpleAlert(Map<Integer, MasterTableTemp1> M1M2Map, Boolean requiredM1, Boolean requiredM2) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {

        List<Integer> benchmarks = new ArrayList<>();
        List<Class> items = new ArrayList<>();
        Boolean isM1 = null, isM2 = null;
        List<FoEscalationTickets> foEscalationTicketsList = new ArrayList<>();

        //Getting list of all class extending SimpleAlert Class
        List<Class<? extends SimpleAlert>> simpleAlertList = getSimpleAlertClassesList();

        //Getting map
        Map<String, List<Integer>> alertVehicleRecipientMap = issueBenchmarkService.getAlertVehicleRecipients();

        Map<Integer, Map<Integer, List<Issues>>> matrix = issueBenchmarkService.getIssueBenchmark();

        for (Integer res1 : M1M2Map.keySet()) {

            String key = res1 + "#" + M1M2Map.get(res1).getVFo();

            if (alertVehicleRecipientMap.containsKey(key)) {

                List<Integer> foidList = alertVehicleRecipientMap.get(key);

                for (int k = 0; k < foidList.size(); k++) {

                    if (matrix.get(foidList.get(k)) != null) {

                        Set<Integer> keysOfFoId = matrix.get(foidList.get(k)).keySet();
                        List<Integer> alertIssueType = new ArrayList<>();
                        for (Integer alert : keysOfFoId) {
                            alertIssueType.add(alert);
                        }

                        for (int i = 0; i < alertIssueType.size(); i++) {

                            String alertClass = "com.elogist.vehicle_master_and_alert_creation.models.alerts.simplealert." + "SimpleAlert" + alertIssueType.get(i);
                            Class simpleAlertClass = StringToClass.getClassName(alertClass);
                            if (simpleAlertClass != null && simpleAlertList.contains(simpleAlertClass)) {
                                SimpleAlert alertClassObj = (SimpleAlert) simpleAlertClass.getDeclaredConstructor().newInstance();
                                applicationContext.getAutowireCapableBeanFactory().autowireBean(alertClassObj);
                                Integer alertTypeId = alertClassObj.getAlertTypeId();
                                List<Issues> alert = matrix.get(foidList.get(k)).get(alertTypeId);
                                for (Issues issueElement : alert) {

                                    Integer foId = foidList.get(k);

                                    asyncService.getSimpleAlert(M1M2Map, issueElement, alertClassObj, requiredM1, requiredM2, foId, res1);


                                }
                            }
                        }
                    }

                }
            }
        }
        return foEscalationTicketsList;
    }

    public void simpleAlertProcessing(Map<Integer, MasterTableTemp1> M1M2Map, Issues issueElement, SimpleAlert alertClassObj, Boolean requiredM1, Boolean requiredM2, Integer foId, Integer res1){


        Boolean isM1 = null, isM2 = null;
        try {
            Boolean resultForM1 = isIssueConstraintValid((Master) M1M2Map.get(res1), issueElement);

            Boolean resultForM2 = isIssueConstraintValid((Master) M1M2Map.get(res1).getMasterTableTemp2(), issueElement);

            if(alertClassObj.isM1M2Valid(M1M2Map.get(res1))) {

                if (resultForM1) {

                    isM1 = alertClassObj.isValidAlert(M1M2Map.get(res1), issueElement);

                    if (resultForM2) {

                        isM2 = alertClassObj.isValidAlert(M1M2Map.get(res1).getMasterTableTemp2(), issueElement);

                        if (isM1 != null && isM2 != null && isM1 == requiredM1 && isM2 == requiredM2) {
                            MasterTableTemp1 masterTableTemp1 = M1M2Map.get(res1);
                            Alerts.SpecificParameters specifiedParameter = alertClassObj.getSpecifiedParameter();
                            String generalParameter = masterTableTemp1Repository.getGeneralParameter(M1M2Map.get(res1).getVId());
                            Alerts.Benchmarks benchmarks = issueElement.getFinalBenchmark(alertClassObj);
                            FoEscalationTickets foEscalationTickets = new FoEscalationTickets(M1M2Map.get(res1), alertClassObj, issueElement, foId, getDeserializeSpecificParam(specifiedParameter), generalParameter, getDeseriliazeBenchmarks(benchmarks));
                            saveIntoFoEscalationTickets(foEscalationTickets);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info(alertClassObj.getAlertTypeId() + " " + M1M2Map.get(res1).getVId() + " Message -> " + e.getMessage());
        }
    }

    public String getM1Data(MasterTableTemp1 masterTableTemp1){

        if(masterTableTemp1 != null) {
            Gson gson = new Gson();
            String m1Result = gson.toJson(masterTableTemp1);
            return m1Result;
        }

        return null;
    }

    public String getM2Data(MasterTableTemp2 masterTableTemp2){

        if (masterTableTemp2 != null) {

            Gson gson = new Gson();
            String m2Result = gson.toJson(masterTableTemp2);

            return m2Result;
        }

        return null;

    }

    public List<Class> getCommonAlertClass(Set<Integer> keysOfFoId, List<Class<? extends SimpleAlert>> res) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class> items = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {

            Constructor<?> ctor = res.get(i).getConstructor();
            SimpleAlert simpleAlert = (SimpleAlert) ctor.newInstance(new Object[]{});
            if (keysOfFoId.contains(simpleAlert.getAlertTypeId())) {

                items.add(res.get(i));
            }
        }
        return items;
    }

    public static Boolean isIssueConstraintValid(Master master, Issues list) {
        Boolean isContinue = true;
        isContinue = checkForVehicle(master.getVId(), list.getVehicles());
        if (isContinue) {
            isContinue = checkForGroups(master.getVgGrpId(), list.getGroups());
        }
        if (isContinue) {
            isContinue = checkForGroups(master.getHlSiteId(), list.getSources());
        }
        if (isContinue) {
            isContinue = checkForGroups(master.getPSiteId(), list.getDestinations());
        }
        return isContinue;
    }

    public static Boolean checkForVehicle(Integer vId, List<Integer> vehicles) {
        if (vehicles.size() > 0) {
            return vehicles.contains(vId);
        } else {
            return true;
        }
    }

    public static Boolean checkForGroups(List<Integer> items, List<Integer> groups) {
        if (groups.size() > 0) {
            items = items == null ? new ArrayList<>() : items;
            for (Integer res : items) {
                if (groups.contains(res)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }



    public void saveIntoFoEscalation(List<FoEscalationTickets> foEscalationTicketsList){


        for(FoEscalationTickets foEscalationTickets : foEscalationTicketsList){

            generateAlertMap(foEscalationTickets);

            sendMessageToKafka(foEscalationTickets);
        }
        foEscalationTicketsRepository.saveAll(foEscalationTicketsList);
    }

    public void saveIntoFoEscalationTickets(FoEscalationTickets foEscalationTickets){

        generateAlertMap(foEscalationTickets);

        sendMessageToKafka(foEscalationTickets);

        foEscalationTicketsRepository.save(foEscalationTickets);

    }

    public void generateAlertMap(FoEscalationTickets foEscalationTickets){
        if(alertsMap.containsKey(foEscalationTickets.getFoIssueTypeId())){
            Integer count = alertsMap.get(foEscalationTickets.getFoIssueTypeId());
            alertsMap.put(foEscalationTickets.getFoIssueTypeId(), count+1);
        }
        else{
            alertsMap.put(foEscalationTickets.getFoIssueTypeId(), 1);
        }

    }

    public void sendMessageToKafka(FoEscalationTickets foEscalationTickets){

        Gson gson = new Gson();

        LOGGER.info("Alert DsaveIntoFoEscalationetails ----> VehicleId: "+ foEscalationTickets.getVehicleId() + "FoIssueTypeId: " + foEscalationTickets.getFoIssueTypeId() + "FoId: " + foEscalationTickets.getFoId() + "Remearks: " + foEscalationTickets.getRemark());

        alertKafkaProducer.sendMessageToNewKafka(gson.toJson(foEscalationTickets),kafkaAlertTopic,foEscalationTickets.getVehicleId().toString());
    }


    public void updateFoEscalation(List<FoEscalationTickets> foEscalationTicketsList){

        foEscalationTicketsRepository.saveAll(foEscalationTicketsList);
    }

    public MasterTableTemp1 getM1ByVehicleId(Integer vehicleId){

        Optional<MasterTableTemp1> masterTableTemp1Opt = masterTableTemp1Repository.findById(vehicleId);

        MasterTableTemp1 masterTableTemp1 = null;

        if(masterTableTemp1Opt.isPresent()) {
            masterTableTemp1 = masterTableTemp1Opt.get();
        }

        return masterTableTemp1;

    }

    public MasterTableTemp2 getM2ByVehicleId(Integer vehicleId){

        MasterTableTemp2 masterTableTemp2 = null;

        Optional<MasterTableTemp2> masterTableTemp2Opt = masterTableTemp2Repository.findById(vehicleId);

        if (masterTableTemp2Opt.isPresent()){
            masterTableTemp2 = masterTableTemp2Opt.get();
        }

        return masterTableTemp2;
    }

    public String getDeseriliazeBenchmarks(Alerts.Benchmarks benchmarks){

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        String deseriliazeBenchmark = gson.toJson(benchmarks);

        return deseriliazeBenchmark;

    }

    public String getDeserializeSpecificParam(Alerts.SpecificParameters specificParameters){

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        String specificParameter = gson.toJson(specificParameters);

        return specificParameter;

    }




}

