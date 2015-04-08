package rafakob.multiedip.utilities;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import rafakob.multiedip.R;
import rafakob.multiedip.bus.LoadDataFinishedEvent;
import rafakob.multiedip.idsys.IdData;

/**
 * Author: Rafal Kobylko
 * Created on: 2015-03-26
 */
public class LoadDataFromFileTask extends AsyncTask<IdData, Long, IdData> {
    private TextView textViewInfo;
    private static final String SEPARATOR = ";";


    public LoadDataFromFileTask(TextView textView) {
        this.textViewInfo = textView;
    }


    @Override
    protected IdData doInBackground(IdData... params) {
        int i = 0;
        IdData iddata = params[0];
        publishProgress();
        updateLengthAndType(iddata);

        double[] output = new double[(int)iddata.getLength()];
        double[] input = new double[(int)iddata.getLength()];


        try {
            BufferedReader br = new BufferedReader(new FileReader(iddata.getPath()));
            String line;

            if(iddata.getType().equals("timeseries")){
                /** Read timeseries **/
                while ((line = br.readLine()) != null) {
                    output[i++] = Double.valueOf(line.trim());

                }
                iddata.setOutput(output);
            }
            else{
                /** Read SISO **/
                while ((line = br.readLine()) != null) {
                    output[i] = Double.valueOf(line.split(SEPARATOR)[0].trim());
                    input[i] = Double.valueOf(line.split(SEPARATOR)[1].trim());
                    i++;
                }
                iddata.setOutput(output);
                iddata.setInput(input);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return iddata;

    }

    private void updateLengthAndType(IdData iddata) {
        long numberOfRows = 0;
        File file = new File(iddata.getPath());
        try {
            iddata.setType(Files.readFirstLine(file, Charsets.UTF_8).contains(SEPARATOR) ? "siso" : "timeseries");

            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.readLine() != null) {
                numberOfRows++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        iddata.setLength(numberOfRows);
    }

    @Override
    protected void onPostExecute(IdData idData) {
        super.onPostExecute(idData);
        EventBus bus = EventBus.getDefault();
        bus.post(new LoadDataFinishedEvent(idData));
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        textViewInfo.setText(textViewInfo.getContext().getString(R.string.loading_file));
    }
}