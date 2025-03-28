package com.example.average_calculator.service;

import com.example.average_calculator.model.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class NumberService {

	@Value("${api.prime}")
	private String primeApi;

	@Value("${api.fibo}")
	private String fibonacciApi;

	@Value("${api.even}")
	private String evenApi;

	@Value("${api.rand}")
	private String randomApi;

	@Value("${window.size}")
	private int windowSize;

	@Value("${api.token}")
	private String apiToken;

	private final RestTemplate restTemplate = new RestTemplate();
	private final LinkedHashSet<Integer> window = new LinkedHashSet<>();

	private static List<Response> previousResponseStore = new ArrayList<Response>();

	// Process the numbers based on numberId
	public Response processNumbers(String numberId) {
		String apiUrl = getApiUrl(numberId);

		if (apiUrl == null) {
			throw new IllegalArgumentException("Invalid numberId. Use 'p', 'f', 'e', or 'r'.");
		}

		List<Integer> prevWindowState = new ArrayList<>(window);

		if (previousResponseStore.size() > 0) {
			Response resp = previousResponseStore.get(0);
			prevWindowState = resp.getWindowCurrState();
			previousResponseStore.remove(0);
		}
		List<Integer> numbers = fetchNumbers(apiUrl);

		updateWindow(numbers);

		List<Integer> currWindowState = new ArrayList<>(window);
		double average = calculateAverage(currWindowState);

		// Return the Response object

		Response response = new Response(prevWindowState, currWindowState, numbers, average);

		previousResponseStore.add(response);

		return response;
	}

	// Get the API URL based on numberId
	private String getApiUrl(String numberId) {
		switch (numberId) {
		case "p":
			return primeApi;
		case "f":
			return fibonacciApi;
		case "e":
			return evenApi;
		case "r":
			return randomApi;
		default:
			return null;
		}
	}

	// Fetch numbers from 3rd party API
	private List<Integer> fetchNumbers(String apiUrl) {
		HttpHeaders headers = new HttpHeaders();

		// Set Authorization Header with the Token
		headers.set("Authorization", "Bearer " + apiToken);

		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

		if (response.getBody() != null && response.getBody().containsKey("numbers")) {
			return (List<Integer>) response.getBody().get("numbers");
		}
		return Collections.emptyList();
	}

	// Update the window with new numbers
	private void updateWindow(List<Integer> numbers) {
		for (int num : numbers) {
			if (window.size() >= windowSize) {
				Iterator<Integer> it = window.iterator();
				it.next();
				it.remove();
			}
			window.add(num);
		}
	}

	// Calculate the average of window numbers
	private double calculateAverage(List<Integer> windowState) {
		if (windowState.isEmpty()) {
			return 0.0;
		}
		int sum = windowState.stream().mapToInt(Integer::intValue).sum();
		return (double) sum / windowState.size();
	}
}
