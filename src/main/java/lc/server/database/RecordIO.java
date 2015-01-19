package lc.server.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lc.common.util.math.ChunkPos;
import lc.server.StargateAddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RecordIO {

	public static class RecordTypeAdapter extends TypeAdapter<StargateRecord> {

		@Override
		public void write(JsonWriter out, StargateRecord value) throws IOException {
			out.beginObject();
			out.name("address").value(value.address.getAddressString());
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
			result.address = new StargateAddress(in.nextString().toCharArray());
			boolean hasCoords = in.nextBoolean();
			if (hasCoords) {
				result.dimension = in.nextInt();
				result.chunk = new ChunkPos(in.nextInt(), in.nextInt());
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
		gson.toJson(records, List.class, outputStream);
		outputStream.close();
	}

	public void readMap(InputStream stream, List<StargateRecord> records) throws IOException {
		JsonReader inputStream = new JsonReader(new InputStreamReader(stream, "UTF-8"));
		records = gson.fromJson(inputStream, List.class);
		inputStream.close();
	}

}
