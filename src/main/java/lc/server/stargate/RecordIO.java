package lc.server.stargate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lc.api.stargate.StargateAddress;
import lc.common.util.math.ChunkPos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RecordIO {

	public static class RecordTypeAdapter extends TypeAdapter<StargateRecord> {

		@Override
		public void write(JsonWriter out, StargateRecord value) throws IOException {
			out.beginObject();
			out.name("address").value(value.address.getAddressString());
			out.name("hasServer").value(value.server != null);
			if (value.server != null)
				out.name("server").value(value.server);
			out.name("hasCoords").value(value.chunk != null);
			if (value.chunk != null) {
				out.name("dimension").value(value.dimension);
				out.name("x").value(value.chunk.cx);
				out.name("z").value(value.chunk.cz);
			}
			out.endObject();
		}

		@Override
		public StargateRecord read(JsonReader in) throws IOException {
			in.beginObject();
			StargateRecord result = new StargateRecord();
			in.nextName();
			result.address = new StargateAddress(in.nextString().toCharArray());
			in.nextName();
			boolean hasServer = in.nextBoolean();
			if (hasServer) {
				in.nextName();
				result.server = in.nextString();
			}
			in.nextName();
			boolean hasCoords = in.nextBoolean();
			if (hasCoords) {
				in.nextName();
				result.dimension = in.nextInt();
				in.nextName();
				int cx = in.nextInt();
				in.nextName();
				int cz = in.nextInt();
				result.chunk = new ChunkPos(cx, cz);
			}
			in.endObject();
			return result;
		}
	}

	public static final RecordTypeAdapter RECORD_ADAPTER = new RecordTypeAdapter();
	Gson gson = new Gson();

	public RecordIO() {
		gson = new GsonBuilder().registerTypeAdapter(StargateRecord.class, RECORD_ADAPTER).create();
	}

	public void writeMap(OutputStream stream, List<StargateRecord> records) throws IOException {
		JsonWriter outputStream = new JsonWriter(new OutputStreamWriter(stream, "UTF-8"));
		Type listType = new TypeToken<ArrayList<StargateRecord>>() {
		}.getType();
		gson.toJson(records, listType, outputStream);
		outputStream.close();
	}

	public ArrayList<StargateRecord> readMap(InputStream stream) throws IOException {
		JsonReader inputStream = new JsonReader(new InputStreamReader(stream, "UTF-8"));
		Type listType = new TypeToken<ArrayList<StargateRecord>>() {
		}.getType();
		ArrayList<StargateRecord> records = gson.fromJson(inputStream, listType);
		inputStream.close();
		return records;
	}

}
