package gg.bot.bottg.config;


import gg.bot.bottg.data.entity.Hosts;
import gg.bot.bottg.data.repository.HostsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class HostGeneratedConfig {

    private final String STANDART = "Стандарт";
    private final String PLUS_STANDART = "Стандарт+";
    private final String VIP = "VIP";
    private final HostsRepository hostsRepository;

    public HostGeneratedConfig(HostsRepository hostsRepository) {
        this.hostsRepository = hostsRepository;
    }

    @PostConstruct
    public void createHosts() {

        if (hostsRepository.findAll().isEmpty()) {

            List<Hosts> hostsList = new ArrayList<>();

            Hosts hosts1 = new Hosts();
            hosts1.setHostGroup(PLUS_STANDART);
            hosts1.setHostNumber(1L);
            hosts1.setHostId(29L);
            hostsList.add(hosts1);

            Hosts hosts2 = new Hosts();
            hosts2.setHostGroup(PLUS_STANDART);
            hosts2.setHostNumber(2L);
            hosts2.setHostId(19L);
            hostsList.add(hosts2);

            Hosts hosts3 = new Hosts();
            hosts3.setHostGroup(PLUS_STANDART);
            hosts3.setHostNumber(3L);
            hosts3.setHostId(37L);
            hostsList.add(hosts3);

            Hosts hosts4 = new Hosts();
            hosts4.setHostGroup(PLUS_STANDART);
            hosts4.setHostNumber(4L);
            hosts4.setHostId(8L);
            hostsList.add(hosts4);

            Hosts hosts5 = new Hosts();
            hosts5.setHostGroup(PLUS_STANDART);
            hosts5.setHostNumber(5L);
            hosts5.setHostId(5L);
            hostsList.add(hosts5);

            Hosts hosts6 = new Hosts();
            hosts6.setHostGroup(PLUS_STANDART);
            hosts6.setHostNumber(6L);
            hosts6.setHostId(9L);
            hostsList.add(hosts6);

            Hosts hosts7 = new Hosts();
            hosts7.setHostGroup(PLUS_STANDART);
            hosts7.setHostNumber(7L);
            hosts7.setHostId(10L);
            hostsList.add(hosts7);

            Hosts hosts8 = new Hosts();
            hosts8.setHostGroup(PLUS_STANDART);
            hosts8.setHostNumber(8L);
            hosts8.setHostId(20L);
            hostsList.add(hosts8);

            Hosts hosts9 = new Hosts();
            hosts9.setHostGroup(PLUS_STANDART);
            hosts9.setHostNumber(9L);
            hosts9.setHostId(23L);
            hostsList.add(hosts9);

            Hosts hosts10 = new Hosts();
            hosts10.setHostGroup(PLUS_STANDART);
            hosts10.setHostNumber(10L);
            hosts10.setHostId(30L);
            hostsList.add(hosts10);

            Hosts hosts11 = new Hosts();
            hosts11.setHostGroup(STANDART);
            hosts11.setHostNumber(11L);
            hosts11.setHostId(24L);
            hostsList.add(hosts11);

            Hosts hosts12 = new Hosts();
            hosts12.setHostGroup(STANDART);
            hosts12.setHostNumber(12L);
            hosts12.setHostId(25L);
            hostsList.add(hosts12);

            Hosts hosts13 = new Hosts();
            hosts13.setHostGroup(STANDART);
            hosts13.setHostNumber(13L);
            hosts13.setHostId(11L);
            hostsList.add(hosts13);

            Hosts hosts14 = new Hosts();
            hosts14.setHostGroup(STANDART);
            hosts14.setHostNumber(14L);
            hosts14.setHostId(26L);
            hostsList.add(hosts14);

            Hosts hosts15 = new Hosts();
            hosts15.setHostGroup(PLUS_STANDART);
            hosts15.setHostNumber(15L);
            hosts15.setHostId(17L);
            hostsList.add(hosts15);

            Hosts hosts16 = new Hosts();
            hosts16.setHostGroup(STANDART);
            hosts16.setHostNumber(16L);
            hosts16.setHostId(6L);
            hosts16.setTime("не работает");
            hostsList.add(hosts16);

            Hosts hosts17 = new Hosts();
            hosts17.setHostGroup(PLUS_STANDART);
            hosts17.setHostNumber(17L);
            hosts17.setHostId(34L);
            hostsList.add(hosts17);

            Hosts hosts18 = new Hosts();
            hosts18.setHostGroup(PLUS_STANDART);
            hosts18.setHostNumber(18L);
            hosts18.setHostId(35L);
            hostsList.add(hosts18);

            Hosts hosts19 = new Hosts();
            hosts19.setHostGroup(PLUS_STANDART);
            hosts19.setHostNumber(19L);
            hosts19.setHostId(36L);
            hostsList.add(hosts19);

            Hosts hosts20 = new Hosts();
            hosts20.setHostGroup(PLUS_STANDART);
            hosts20.setHostNumber(20L);
            hosts20.setHostId(33L);
            hostsList.add(hosts20);

            Hosts hosts21 = new Hosts();
            hosts21.setHostGroup(PLUS_STANDART);
            hosts21.setHostNumber(21L);
            hosts21.setHostId(32L);
            hostsList.add(hosts21);

            Hosts hosts22 = new Hosts();
            hosts22.setHostGroup(PLUS_STANDART);
            hosts22.setHostNumber(22L);
            hosts22.setHostId(39L);
            hostsList.add(hosts22);

            Hosts hosts23 = new Hosts();
            hosts23.setHostGroup(VIP);
            hosts23.setHostNumber(23L);
            hosts23.setHostId(7L);
            hostsList.add(hosts23);

            Hosts hosts24 = new Hosts();
            hosts24.setHostGroup(VIP);
            hosts24.setHostNumber(24L);
            hosts24.setHostId(12L);
            hostsList.add(hosts24);

            Hosts hosts25 = new Hosts();
            hosts25.setHostGroup(VIP);
            hosts25.setHostNumber(25L);
            hosts25.setHostId(13L);
            hostsList.add(hosts25);

            Hosts hosts26 = new Hosts();
            hosts26.setHostGroup(VIP);
            hosts26.setHostNumber(26L);
            hosts26.setHostId(21L);
            hostsList.add(hosts26);

            Hosts hosts27 = new Hosts();
            hosts27.setHostGroup(VIP);
            hosts27.setHostNumber(27L);
            hosts27.setHostId(22L);
            hostsList.add(hosts27);

            Hosts hosts28 = new Hosts();
            hosts28.setHostGroup(PLUS_STANDART);
            hosts28.setHostNumber(28L);
            hosts28.setHostId(50L);
            hostsList.add(hosts28);

            Hosts hosts29 = new Hosts();
            hosts29.setHostGroup(PLUS_STANDART);
            hosts29.setHostNumber(29L);
            hosts29.setHostId(40L);
            hostsList.add(hosts29);

            Hosts hosts30 = new Hosts();
            hosts30.setHostGroup(PLUS_STANDART);
            hosts30.setHostNumber(30L);
            hosts30.setHostId(43L);
            hostsList.add(hosts30);

            Hosts hosts31 = new Hosts();
            hosts31.setHostGroup(PLUS_STANDART);
            hosts31.setHostNumber(31L);
            hosts31.setHostId(45L);
            hostsList.add(hosts31);

            Hosts hosts32 = new Hosts();
            hosts32.setHostGroup(PLUS_STANDART);
            hosts32.setHostNumber(32L);
            hosts32.setHostId(49L);
            hostsList.add(hosts32);

            Hosts hosts33 = new Hosts();
            hosts33.setHostGroup(PLUS_STANDART);
            hosts33.setHostNumber(33L);
            hosts33.setHostId(41L);
            hostsList.add(hosts33);

            Hosts hosts34 = new Hosts();
            hosts34.setHostGroup(PLUS_STANDART);
            hosts34.setHostNumber(34L);
            hosts34.setHostId(46L);
            hostsList.add(hosts34);

            Hosts hosts35 = new Hosts();
            hosts35.setHostGroup(PLUS_STANDART);
            hosts35.setHostNumber(35L);
            hosts35.setHostId(47L);
            hostsList.add(hosts35);

            Hosts hosts36 = new Hosts();
            hosts36.setHostGroup(PLUS_STANDART);
            hosts36.setHostNumber(36L);
            hosts36.setHostId(48L);
            hostsList.add(hosts36);

            Hosts hosts37 = new Hosts();
            hosts37.setHostGroup(PLUS_STANDART);
            hosts37.setHostNumber(37L);
            hosts37.setHostId(31L);
            hostsList.add(hosts37);

            Hosts hosts38 = new Hosts();
            hosts38.setHostGroup(PLUS_STANDART);
            hosts38.setHostNumber(38L);
            hosts38.setHostId(42L);
            hostsList.add(hosts38);

            Hosts hosts39 = new Hosts();
            hosts39.setHostGroup(PLUS_STANDART);
            hosts39.setHostNumber(39L);
            hosts39.setHostId(44L);
            hostsList.add(hosts39);

            hostsRepository.saveAll(hostsList);

            log.info("\u001B[32m" + "HostSList has been created");
        }
    }
}
