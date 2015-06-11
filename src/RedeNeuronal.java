import com.googlecode.fannj.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andr� on 28/05/2015.
 */


public class RedeNeuronal {

    Fann fann;

    public RedeNeuronal() throws IOException {
        criarRede(11, 16, 2);
        treinarRede("/Users/andrepcg/WifiGPS/train.data");

        float[] out = runInputs(new float[]{0.41333333f, 0.2f, 0.0f, 0.2f, 0.28f, 0.2f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f});
        System.out.println("Expected: " + (int)(16) + " " + (int)(9));
        System.out.println("Got:" + (int)(out[0] * 35) +" " + (int)(out[1] * 22));

    }

    public void carregarRede(){
        fann = new Fann("/Users/andrepcg/WifiGPS/bestann");
    }

    public void criarRede(int inputNeurons, int hiddenNeurons, int outputNeurons) throws IOException {

        //IOUtils.copy(this.getClass().getResourceAsStream("xor.data"), new FileOutputStream(temp));
        List<Layer> layers = new ArrayList<Layer>();
        layers.add(Layer.create(inputNeurons));
        layers.add(Layer.create(hiddenNeurons));
        layers.add(Layer.create(outputNeurons));
        fann = new Fann(layers);

    }

    public void treinarRede(String trainFile) throws IOException {
        Trainer trainer = new Trainer(fann);

        float desiredError = .0009f;
        trainer.setTrainingAlgorithm(TrainingAlgorithm.FANN_TRAIN_INCREMENTAL);
        //trainer.
        float mse = trainer.train(trainFile, 5000, 200, desiredError);
        System.out.println("MSE: " + mse);
        fann.save("/Users/andrepcg/WifiGPS/fannj.net");
    }

    String readFile(String filename) {
        File f = new File(filename);
        try {
            byte[] bytes = Files.readAllBytes(f.toPath());
            return new String(bytes,"UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public float[] runInputs(float[] inputs){
        return fann.run( inputs );
    }
}
