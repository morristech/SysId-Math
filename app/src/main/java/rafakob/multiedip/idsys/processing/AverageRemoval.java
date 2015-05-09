package rafakob.multiedip.idsys.processing;

import com.google.common.math.DoubleMath;

import rafakob.multiedip.idsys.IdData;

/**
 * Created by Rafal on 2015-03-31.
 */
public class AverageRemoval implements DataProcessingInterface {
    @Override
    public IdData execute(IdData iddata) {
        if(iddata.isSiso()) {
            iddata.setInput(removeAvg(iddata.getInput().clone()));
            iddata.setOutput(removeAvg(iddata.getOutput().clone()));
        } else{
            iddata.setOutput(removeAvg(iddata.getOutput().clone()));
        }
        iddata.setLength(50-5+1);
        return iddata;
    }

    @Override
    public String getFunctionDescription() {
        return "Average removal";
    }

    private double[] removeAvg(double[] v){
        double avg = DoubleMath.mean(v);

        for (int i = 0; i < v.length; i++) {
            v[i] = v[i] - avg;
        }

        return v;
    }
}
