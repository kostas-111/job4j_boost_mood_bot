package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import java.util.ArrayList;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner checkEnv(ApplicationContext ctx) {
        return args -> {
            System.out.println(ctx.getEnvironment().getProperty("telegram.bot.name"));
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramLongPollingBot.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
                System.out.println("Бот успешно зарегистрирован");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

        @Bean
        CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                       MoodContentRepository moodContentRepository,
                                       AwardRepository awardRepository) {

            return args -> {
                var moods = moodRepository.findAll();
                if (!moods.isEmpty()) {
                    return;
                }
                var data = new ArrayList<MoodContent>();
                data.add(new MoodContent(new Mood("Счастливейший на свете \uD83D\uDE0E", true), "Невероятно! Вы сияете от счастья, продолжайте радоваться жизни."));
                data.add(new MoodContent(new Mood("Воодушевленное настроение \uD83C\uDF1F", true), "Великолепно! Вы чувствуете себя на высоте. Продолжайте в том же духе."));
                data.add(new MoodContent(new Mood("Успокоение и гармония \uD83E\uDDD8\u200D♂️", true), "Потрясающе! Вы в состоянии внутреннего мира и гармонии."));
                data.add(new MoodContent(new Mood("В состоянии комфорта ☺️", true), "Отлично! Вы чувствуете себя уютно и спокойно."));
                data.add(new MoodContent(new Mood("Легкое волнение \uD83C\uDF88", true), "Замечательно! Немного волнения добавляет жизни краски."));
                data.add(new MoodContent(new Mood("Сосредоточенное настроение \uD83C\uDFAF", true), "Хорошо! Ваш фокус на высоте, используйте это время эффективно."));
                data.add(new MoodContent(new Mood("Тревожное настроение \uD83D\uDE1F", false), "Не волнуйтесь, всё пройдет. Попробуйте расслабиться и найти источник вашего беспокойства."));
                data.add(new MoodContent(new Mood("Разочарованное настроение \uD83D\uDE1E", false), "Бывает. Не позволяйте разочарованию сбить вас с толку, всё наладится."));
                data.add(new MoodContent(new Mood("Усталое настроение \uD83D\uDE34", false), "Похоже, вам нужен отдых. Позаботьтесь о себе и отдохните."));
                data.add(new MoodContent(new Mood("Вдохновенное настроение \uD83D\uDCA1", true), "Потрясающе! Вы полны идей и энергии для их реализации."));
                data.add(new MoodContent(new Mood("Раздраженное настроение \uD83D\uDE20", false), "Попробуйте успокоиться и найти причину раздражения, чтобы исправить ситуацию."));
                moodRepository.saveAll(data.stream().map(MoodContent::getMood).toList());
                moodContentRepository.saveAll(data);
                var awards = new ArrayList<Award>();
                awards.add(new Award("Смайлик дня",  "Веселый смайлик или стикер, отправленный пользователю в качестве поощрения.", 1));
                awards.add(new Award("Настроение недели", "Специальный значок или иконка, отображаемая в профиле пользователя в течение недели.", 7));
                awards.add(new Award("Бонусные очки", "Очки, которые можно обменять на виртуальные предметы или функции внутри приложения.", 3));
                awards.add(new Award("Персонализированные рекомендации", "Подборка контента или активности на основе интересов пользователя.", 5));
                awards.add(new Award("Достижение 'Солнечный луч'", "Разблокировка новой темы оформления или фона в приложении.", 10));
                awards.add(new Award("Виртуальный подарок", "Возможность отправить или получить виртуальный подарок внутри приложения.", 15));
                awards.add(new Award("Титул 'Лучезарный'", "Специальный титул, отображаемый рядом с именем пользователя.", 20));
                awards.add(new Award("Доступ к премиум-функциям", "Временный доступ к премиум-функциям или эксклюзивному контенту.", 30));
                awards.add(new Award("Участие в розыгрыше призов", "Шанс выиграть призы в ежемесячных розыгрышах.", 7));
                awards.add(new Award("Эксклюзивный контент", "Доступ к эксклюзивным статьям, видео или мероприятиям.", 25));
                awards.add(new Award("Награда 'Настроение месяца'", "Специальный значок, признание в сообществе или дополнительные привилегии.", 30));
                awards.add(new Award("Физический подарок", "Возможность получить небольшой физический подарок, например, открытку или фирменный сувенир.", 60));
                awards.add(new Award("Коучинговая сессия", "Бесплатная сессия с коучем или консультантом для дальнейшего улучшения благополучия.", 45));
                awards.add(new Award("Разблокировка мини-игр", "Доступ к развлекательным мини-играм внутри приложения.", 14));
                awards.add(new Award("Персональное поздравление", "Персонализированное сообщение от команды приложения или вдохновляющая цитата.", 50));
                awardRepository.saveAll(awards);
        };
    }
}
