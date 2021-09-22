package com.corona.demo.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.corona.demo.model.LocationStats;

@Service
public class CoronaVirusDataService {

	private List<LocationStats> allStats = new ArrayList<>();

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	@PostConstruct // whenever application start so this method will be automatically execute.
	@Scheduled(cron = "* * 1 * * *") // this annotation is use to run method on regular basis as data is being
	// updated on regular basis.

	// here there should be 6 star first represent the sec then minute then hour
	// etc.
	// if we use all 6 star then it will execute after 1 sec.
	// if we use @Scheduled(cron = "0 15 10 15 * ?"), then it executed at 10:15 AM
	// on the 15th day of every month.
	// it will execute after 1 hr on each day.

	public void fetchVirusData() throws IOException, InterruptedException {

		List<LocationStats> newStats = new ArrayList<>();

		// use to make the http call.
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		// System.out.println(httpResponse.body());

		StringReader csvBodyReader = new StringReader(httpResponse.body());

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

		for (CSVRecord record : records) {

			LocationStats locationStats = new LocationStats();
			locationStats.setState(record.get("Province/State"));
			locationStats.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size() - 1));
			int prevCases = Integer.parseInt(record.get(record.size() - 2));
			locationStats.setLatestTotalCases(latestCases);
			locationStats.setDiffFromPrevDay(latestCases - prevCases);

			System.out.println(locationStats);
			newStats.add(locationStats);

		}
		this.allStats = newStats;

	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}

}
