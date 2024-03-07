package com.sample;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.IInterceptorService;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

public class SampleClient {
	private long averageTime;
	private long totalTime;
	private int numberOfRequest;

	public static void main(String[] theArgs) throws Exception {

		SampleClient sampleClient = new SampleClient();
		File file = sampleClient.getFileFromResources("patientName.txt");
		sampleClient.readFile(file);
		System.out.println("Average time in mili Second.....["+sampleClient.getAverageTime()+"]");

	}

	public static void sortResource(Bundle response) {
		response.getEntry().sort(new Comparator<BundleEntryComponent>() {
			@Override
			public int compare(BundleEntryComponent p1, BundleEntryComponent p2) {
				Patient patient1 = (Patient) p1.getResource();
				Patient patient2 = (Patient) p2.getResource();
				return (patient1.getName().get(0).getNameAsSingleString()
						.compareTo(patient2.getName().get(0).getNameAsSingleString()));
			}

		});
	}

	public static void sortDescendingResource(Bundle response) {
		response.getEntry().sort(new Comparator<BundleEntryComponent>() {
			@Override
			public int compare(BundleEntryComponent p1, BundleEntryComponent p2) {
				Patient patient1 = (Patient) p1.getResource();
				Patient patient2 = (Patient) p2.getResource();
				return (patient2.getName().get(0).getNameAsSingleString()
						.compareTo(patient1.getName().get(0).getNameAsSingleString()));
			}

		});
	}

	public static void printRecord(Bundle response) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd ");
		for (BundleEntryComponent bundleEntryComponent : response.getEntry()) {
			Patient patient = (Patient) bundleEntryComponent.getResource();
			System.out.println(patient.getName().get(0).getNameAsSingleString() + "     "
					+ (patient.getBirthDate() != null ? dateFormat.format(patient.getBirthDate()) : ""));

		}
	}

	private File getFileFromResources(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}

	public  void readFile(File file) throws Exception {

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String name;
		while ((name = br.readLine()) != null) {
			processRequest(name);
			System.out.println(name);
		}
		br.close();
		fr.close();
	}

	public  void processRequest(String name) {
		IClientInterceptorImp iClientInterceptorImp=new IClientInterceptorImp();
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
		client.registerInterceptor(new LoggingInterceptor(false));
		client.registerInterceptor(iClientInterceptorImp);

		Bundle response = client.search().forResource("Patient").where(Patient.FAMILY.matches().value(name))
				.returnBundle(Bundle.class).execute();
		addAverageTime(iClientInterceptorImp.getDuration().toMillis());
		numberOfRequest=numberOfRequest+1;
		sortResource(response);
		printRecord(response);
		System.out.println("*******************************"+iClientInterceptorImp.getDuration());
	}
	
	public void addAverageTime(long duration) {
		totalTime=totalTime+duration;
	}
	
	public long getAverageTime() {
		return totalTime/numberOfRequest;
	}
	

}
