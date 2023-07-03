package gg.bot.bottg.config;

import com.google.gson.JsonObject;
import gg.bot.bottg.data.entity.Prize;
import gg.bot.bottg.data.repository.PrizeRepository;
import gg.bot.bottg.service.ConnectionGizmoService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PrizeGeneratedConfig {

    private final PrizeRepository prizeRepository;
    public PrizeGeneratedConfig(PrizeRepository prizeRepository) {
        this.prizeRepository = prizeRepository;
    }

    @PostConstruct
    public void createPrizes() {

        if (prizeRepository.findAll().isEmpty()) {

            List<Prize> prizeList = new ArrayList<>();

            Prize prize1 = new Prize();
            prize1.setPrizeDay(1L);
            prize1.setPrizeName("+15 баллов");
            prizeList.add(prize1);

            Prize prize2 = new Prize();
            prize2.setPrizeDay(2L);
            prize2.setPrizeName("+20 баллов");
            prizeList.add(prize2);

            Prize prize3 = new Prize();
            prize3.setPrizeDay(3L);
            prize3.setPrizeName("Утр. пакет Standart+/Vip");
            prizeList.add(prize3);

            Prize prize4 = new Prize();
            prize4.setPrizeDay(4L);
            prize4.setPrizeName("+25 баллов");
            prizeList.add(prize4);

            Prize prize5 = new Prize();
            prize5.setPrizeDay(5L);
            prize5.setPrizeName("+30 баллов");
            prizeList.add(prize5);

            Prize prize6 = new Prize();
            prize6.setPrizeDay(6L);
            prize6.setPrizeName("+35 баллов");
            prizeList.add(prize6);

            Prize prize7 = new Prize();
            prize7.setPrizeDay(7L);
            prize7.setPrizeName("6ч пакет Standart+/Vip");
            prizeList.add(prize7);

            Prize prize8 = new Prize();
            prize8.setPrizeDay(8L);
            prize8.setPrizeName("+20 баллов");
            prizeList.add(prize8);

            Prize prize9 = new Prize();
            prize9.setPrizeDay(9L);
            prize9.setPrizeName("+25 баллов");
            prizeList.add(prize9);

            Prize prize10 = new Prize();
            prize10.setPrizeDay(10L);
            prize10.setPrizeName("4ч пакет Standart+/Vip");
            prizeList.add(prize10);

            Prize prize11 = new Prize();
            prize11.setPrizeDay(11L);
            prize11.setPrizeName("+30 баллов");
            prizeList.add(prize11);

            Prize prize12 = new Prize();
            prize12.setPrizeDay(12L);
            prize12.setPrizeName("+35 баллов");
            prizeList.add(prize12);

            Prize prize13 = new Prize();
            prize13.setPrizeDay(13L);
            prize13.setPrizeName("+40 баллов");
            prizeList.add(prize13);

            Prize prize14 = new Prize();
            prize14.setPrizeDay(14L);
            prize14.setPrizeName("6ч пакет Standart+/Vip");
            prizeList.add(prize14);

            Prize prize15 = new Prize();
            prize15.setPrizeDay(15L);
            prize15.setPrizeName("+25 баллов");
            prizeList.add(prize15);

            Prize prize16 = new Prize();
            prize16.setPrizeDay(16L);
            prize16.setPrizeName("+30 баллов");
            prizeList.add(prize16);

            Prize prize17 = new Prize();
            prize17.setPrizeDay(17L);
            prize17.setPrizeName("4ч пакет Standart+/Vip");
            prizeList.add(prize17);

            Prize prize18 = new Prize();
            prize18.setPrizeDay(18L);
            prize18.setPrizeName("+35 баллов");
            prizeList.add(prize18);

            Prize prize19 = new Prize();
            prize19.setPrizeDay(19L);
            prize19.setPrizeName("+40 баллов");
            prizeList.add(prize19);

            Prize prize20 = new Prize();
            prize20.setPrizeDay(20L);
            prize20.setPrizeName("+45 баллов");
            prizeList.add(prize20);

            Prize prize21 = new Prize();
            prize21.setPrizeDay(21L);
            prize21.setPrizeName("Ночной пакет Standart+/Vip либо любой другой на выбор");
            prizeList.add(prize21);

            Prize prize22 = new Prize();
            prize22.setPrizeDay(22L);
            prize22.setPrizeName("+30 баллов");
            prizeList.add(prize22);

            Prize prize23 = new Prize();
            prize23.setPrizeDay(23L);
            prize23.setPrizeName("+35 баллов");
            prizeList.add(prize23);

            Prize prize24 = new Prize();
            prize24.setPrizeDay(24L);
            prize24.setPrizeName("6ч пакет Standart+/Vip");
            prizeList.add(prize24);

            Prize prize25 = new Prize();
            prize25.setPrizeDay(25L);
            prize25.setPrizeName("+40 баллов");
            prizeList.add(prize25);

            Prize prize26 = new Prize();
            prize26.setPrizeDay(26L);
            prize26.setPrizeName("+45 баллов");
            prizeList.add(prize26);

            Prize prize27 = new Prize();
            prize27.setPrizeDay(27L);
            prize27.setPrizeName("+50 баллов");
            prizeList.add(prize27);

            Prize prize28 = new Prize();
            prize28.setPrizeDay(28L);
            prize28.setPrizeName("Ночной пакет Standart+/Vip либо любой другой на выбор");
            prizeList.add(prize28);

            prizeRepository.saveAll(prizeList);

            log.info("\u001B[32m" + "PrizeList has been created");
        }
    }
}
