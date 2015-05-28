import com.googlecode.fannj.*;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by André on 28/05/2015.
 */


public class RedeNeuronal {

    Fann fann;

    public RedeNeuronal(){

    }

    public void carregarRede(){
        fann = new Fann("fannj_.net");
    }

    public void criarRede(int inputNeurons, int hiddenNeurons) throws IOException {

        //IOUtils.copy(this.getClass().getResourceAsStream("xor.data"), new FileOutputStream(temp));
        List<Layer> layers = new ArrayList<Layer>();
        layers.add(Layer.create(inputNeurons));
        layers.add(Layer.create(hiddenNeurons, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
        layers.add(Layer.create(1, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
        fann = new Fann(layers);

    }

    public void treinarRede(String trainFile) throws IOException {
        File temp = File.createTempFile("fannj_", ".net");

        Trainer trainer = new Trainer(fann);

        float desiredError = .1f;
        trainer.setTrainingAlgorithm(TrainingAlgorithm.FANN_TRAIN_INCREMENTAL);
        trainer.train(trainFile, 1000, 100, desiredError);
        fann.save(temp.toString());
    }

    public float[] runInputs(float[] inputs){
        return fann.run( inputs );
    }
}
