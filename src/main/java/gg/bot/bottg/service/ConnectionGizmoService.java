package gg.bot.bottg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

@Component
public class ConnectionGizmoService {

    @Value("${gizmo_url}")
    private String gizmoUrl;

    @Value("${gizmo_login}")
    private String gizmoLogin;

    @Value("${gizmo_password}")
    private String gizmoPassword;


    public String getToken() {

        String urlAuth = gizmoUrl + "/auth/token?username=" + gizmoLogin + "&password=" + gizmoPassword;
        String token = null;
        try {
            URL url = new URL(urlAuth);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(connection.getInputStream());
            token = node.get("token").toString().replaceAll("\"", "");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public JsonObject connectionGet(String token, String url) {

        HttpURLConnection conn = getHttpURLConnection(token, url);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String output;

        StringBuilder response = new StringBuilder();
        while (true) {
            try {
                if ((output = in.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response.append(output);
        }

        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Gson().fromJson(String.valueOf(response), JsonObject.class);
    }

    @NotNull
    private HttpURLConnection getHttpURLConnection(String token, String url) {
        URL urlUserSpending = null;

        try {
            urlUserSpending = new URL(gizmoUrl + url);
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlUserSpending.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        conn.setRequestProperty("Authorization","Bearer "+ token);
        conn.setRequestProperty("Content-Type","application/json");
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public void connectionPut(String token, String url) {

        url = gizmoUrl + url;
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPut put = new HttpPut(url);
        put.addHeader("Authorization","Bearer "+ token);
        try {
            HttpResponse response = httpClient.execute(put);
            System.out.println(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
