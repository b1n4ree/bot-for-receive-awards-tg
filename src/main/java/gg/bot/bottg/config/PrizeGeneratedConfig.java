package gg.bot.bottg.config;

import gg.bot.bottg.data.entity.Prize;
import gg.bot.bottg.data.repository.PrizeRepository;
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

    @Bean
    public void createPrizes() {

        List<Prize> prizeList = new ArrayList<>();

        for (long i = 0L; i < 30; i++) {

            Prize prize = new Prize();
            prize.setId(i);
            prize.setPrizeName("prize #" + i);
            prize.setPrizeDay(i);
            prizeList.add(prize);
        }
        prizeRepository.saveAll(prizeList);
        log.info("\u001B[32m" + "PrizeList has been created. Size[" + prizeList.size() + "]");
    }
}
