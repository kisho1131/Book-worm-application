package com.bookworm.bookwormapplicationdataloader;

import com.bookworm.bookwormapplicationdataloader.author.Author;
import com.bookworm.bookwormapplicationdataloader.author.AuthorRepository;
import com.bookworm.bookwormapplicationdataloader.connection.DataStaxAstraProperties;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import javax.annotation.PostConstruct;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookWormApplicationDataLoaderApplication {

	@Autowired
	AuthorRepository authorRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BookWormApplicationDataLoaderApplication.class, args);
	}

	private void initAuthor(){
		Path path = Paths.get(authorDumpLocation);
		// Stream<String> lines = Files.lines(path);
		try(Stream<String> lines = Files.lines(path)){
			// 1. Read the Line
			lines.forEach(line -> {
				String jsonString = line.substring(line.indexOf("{"));
				try {
					// 2. Construct author object
					JSONObject jsonObject = new JSONObject(jsonString);
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
					// 3. Persist Using Repository
					System.out.println("Saving Author " + author.getName() + " .......");
					authorRepository.save(author);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void initWorks(){

	}

	@PostConstruct
	public void start(){
		// Author author = new Author();
		// author.setId("id");
		// author.setName("name");
		// author.setPersonalName("personalName");
		// authorRepository.save(author);
		initAuthor();
		System.out.println(worksDumpLocation);
	}
	/**
	 * This is necessary to have the Spring Boot app use the Astra secure bundle
	 * to connect to the database
	 */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
