package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.juego.values.JugadorId;
import co.com.sofka.cargame.domain.juego.values.Nombre;
import co.com.sofka.cargame.usecase.model.Score;
import co.com.sofka.cargame.usecase.services.ScoreService;
import com.google.gson.Gson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreQueryService implements ScoreService {

    private final MongoTemplate mongoTemplate;

    public ScoreQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }



    @Override
    public List<Score> getScore() {

       var operation = LookupOperation.newLookup()
                .from("juego.JugadorCreado")
                .localField("jugadorId.uuid")
                .foreignField("jugadorId.uuid").as("player");

       var replace = Aggregation.replaceRoot(ObjectOperators.valueOf(Aggregation.ROOT)
                       .mergeWith(ArrayOperators.ArrayElemAt.arrayOf("player").elementAt(0)));

       var aGroup = Aggregation.group("jugadorId", "nombre").count().as("score");

       var aggregation = Aggregation.newAggregation(operation, replace, aGroup);

       return this.mongoTemplate.aggregate(aggregation, "juego.PrimerLugarAsignado", String.class)
                .getMappedResults()
                .stream()
                .map(res-> new Gson().fromJson(res, ScoreRecord.class))
                .map(scoreRecord ->{
                    return new Score(scoreRecord._id.jugadorId.value(),
                            scoreRecord.get_id().nombre.value(),
                            scoreRecord.score);
                })
                .collect(Collectors.toList());

    }



    public static class  ScoreRecord {

        private JugadorScore _id;
        private Integer score;

        public JugadorScore get_id() {
            return _id;
        }

        public void set_id(JugadorScore _id) {
            this._id = _id;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }


    private static class JugadorScore {

        private JugadorId jugadorId;

        private Nombre nombre;


        public JugadorId getJugadorId() {
            return jugadorId;
        }

        public void setJugadorId(JugadorId jugadorId) {
            this.jugadorId = jugadorId;
        }

        public Nombre getNombre() {
            return nombre;
        }

        public void setNombre(Nombre nombre) {
            this.nombre = nombre;
        }
    }
}
