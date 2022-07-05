package co.com.sofka.cargame.infra.config;

import co.com.sofka.business.generic.ServiceBuilder;
import co.com.sofka.business.generic.UseCase;
import co.com.sofka.cargame.SocketController;
import co.com.sofka.cargame.infra.bus.EventListenerSubscriber;
import co.com.sofka.cargame.infra.bus.EventSubscriber;
import co.com.sofka.cargame.infra.bus.NATSEventSubscriber;
import co.com.sofka.cargame.infra.services.*;
import co.com.sofka.cargame.usecase.listeners.*;
import co.com.sofka.infraestructure.asyn.SubscriberEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Set;

@Configuration
public class JuegoConfig {

    public static final String EXCHANGE = "cargame";

    @Bean
    public SubscriberEvent subscriberEvent(EventStoreRepository eventStoreRepository, EventBus eventBus) {
        return new SubscriberEvent(eventStoreRepository, eventBus);
    }

    @Bean
    public ServiceBuilder serviceBuilder(
            CarrilCarroQueryService carrilCarroService,
            CarroQueryService carroQueryService,
            JuegoQueryService juegoQueryService,
            MoverCarroCommandService moverCarroCommandService,
            ScoreQueryService scoreQueryService
    ) {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.addService(carrilCarroService);
        serviceBuilder.addService(carroQueryService);
        serviceBuilder.addService(juegoQueryService);
        serviceBuilder.addService(moverCarroCommandService);
        serviceBuilder.addService(scoreQueryService);
        return serviceBuilder;
    }

    @Bean
    public Set<UseCase.UseCaseWrap> listUseCasesForListener(
            AsinarAPodioUseCase asinarAPodioUseCase,
            CrearCarrilUseCase crearCarrilUseCase,
            CrearCarroUseCase crearCarroUseCase,
            MotorJuegoUseCase motorJuegoUseCase,
            MoverCarroEnCarrilUseCase moverCarroEnCarrilUseCase,
            NotificarGanadoresUseCase notificarGanadoresUseCase
    ) {
        return Set.of(
                new UseCase.UseCaseWrap("carril.CarroFinalizoSuRecorrido", (UseCase) asinarAPodioUseCase),
                new UseCase.UseCaseWrap("carro.CarroCreado", (UseCase) crearCarrilUseCase),
                new UseCase.UseCaseWrap("juego.JugadorCreado", (UseCase) crearCarroUseCase),
                new UseCase.UseCaseWrap("juego.JuegoIniciado", (UseCase) motorJuegoUseCase),
                new UseCase.UseCaseWrap("carro.KilometrajeCambiado", (UseCase) moverCarroEnCarrilUseCase),
                new UseCase.UseCaseWrap("juego.JuegoFinalizado", (UseCase) notificarGanadoresUseCase)
        );
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        var rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE));
        return rabbitAdmin;
    }

}
