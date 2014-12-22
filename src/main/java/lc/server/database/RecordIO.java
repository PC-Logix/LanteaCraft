package lc.server.database;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RecordIO {

	public static class RecordTypeAdapter extends TypeAdapter<StargateRecord> {

		@Override
		public void write(JsonWriter out, StargateRecord value) throws IOException {
			// TODO: Auto-generated method stub
		}

		@Override
		public StargateRecord read(JsonReader in) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static final RecordTypeAdapter RECORD_ADAPTER = new RecordTypeAdapter();

	Gson gson = new Gson();

	public RecordIO() {
		gson = new GsonBuilder().registerTypeAdapter(StargateRecord.class, RECORD_ADAPTER).create();
	}

	public String writeMap(List<StargateRecord> records) {
		return gson.toJson(records);
	}

	public void readMap(List<StargateRecord> records, String blob) {
		records = gson.fromJson(blob, List.class);
	}

}
