/*
 * Copyright (C) 2024 raph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rommanager.romM;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author raph
 */
public class RomMclient {
    private static final String BASE_URL = "http://localhost/api"; // No trailing slash !!
	private static String TOKEN = "";
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final static Base64 encoder = new Base64();
    private final Gson gson = new Gson();

    public RomMclient(String username, String password) {
        TOKEN = encoder.encodeToString((username + ":" + password).getBytes());
    }
   
    public List<Collection> getCollections() throws IOException, ServerException {
        
        String url = "collections";
		String bodyString = getBodyString(url, client);
		
		List<Collection> fromJson = null;
		if (!bodyString.equals("")) {
            Type collectionListType = new TypeToken<List<Collection>>(){}.getType();
            fromJson = gson.fromJson(bodyString, collectionListType);
		}
		return fromJson;
	}
    
    public List<Platform> getPlatforms() throws IOException, ServerException {
        
        String url = "platforms";
		String bodyString = getBodyString(url, client);
		
		List<Platform> fromJson = null;
		if (!bodyString.equals("")) {
            Type collectionListType = new TypeToken<List<Platform>>(){}.getType();
            fromJson = gson.fromJson(bodyString, collectionListType);
		}
		return fromJson;
	}

    // TODO: Move below a library (used in Slskd in JaMuz too for instance
    
    private HttpUrl.Builder getUrlBuilder(String url) {
        return Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/" + url)).newBuilder(); //NON-NLS
    }

    private Request.Builder getRequestBuilder(HttpUrl.Builder urlBuilder) {
        return new Request.Builder()
				.addHeader("Authorization", "Basic " + TOKEN)
                .url(urlBuilder.build());
    }

    private String getBodyString(String url, OkHttpClient client) throws IOException, ServerException {
        HttpUrl.Builder urlBuilder = getUrlBuilder(url);
        return getBodyString(urlBuilder, client);
    }

    private String getBodyString(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServerException {
        return getBody(urlBuilder, client).string();
    }

    private ResponseBody getBody(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServerException {
        Request request = getRequestBuilder(urlBuilder).build();
        return getBody(request, client);
    }

    private String getBodyString(Request request, OkHttpClient client) throws IOException, ServerException {
        return getBody(request, client).string();
    }

    private ResponseBody getBody(Request request, OkHttpClient client) throws IOException, ServerException {
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
//            if (response.code() == 301) {
//                throw new ServerException(request.header("api-version") + " not supported. " + Objects.requireNonNull(response.body()).string()); //NON-NLS
//            }
            throw new ServerException(response.code() + ": " + response.message());
        }
        return response.body();
    }

	public static class ServerException extends Exception {
        public ServerException(String errorMessage) {
            super(errorMessage);
        }
    }
}
