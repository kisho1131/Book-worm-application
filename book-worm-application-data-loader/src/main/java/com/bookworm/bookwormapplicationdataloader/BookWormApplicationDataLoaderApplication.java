package com.bookworm.bookwormapplicationdataloader;

import com.bookworm.bookwormapplicationdataloader.author.Author;
import com.bookworm.bookwormapplicationdataloader.author.AuthorRepository;
import com.bookworm.bookwormapplicationdataloader.books.Book;
import com.bookworm.bookwormapplicationdataloader.books.BookRepository;
import com.bookworm.bookwormapplicationdataloader.connection.DataStaxAstraProperties;

import org.json.JSONArray;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookWormApplicationDataLoaderApplication {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	BookRepository bookRepository;

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
					System.out.println("Saving Author " + author.getName() + " .............");
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
		Path path = Paths.get(worksDumpLocation);
		DateTimeFormatter dateFormat =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		// 1. Read the Line
		try(Stream<String> lines = Files.lines(path)){
			// 2. Construct the author object
			lines.forEach(line ->{
				String jsonString = line.substring(line.indexOf("{"));
				try{
					// 2. Construct Json Object for Book
					JSONObject jsonObject = new JSONObject(jsonString);
					Book book = new Book();
					book.setId(jsonObject.getString("key").replace("/works/", ""));
					book.setName(jsonObject.optString("title"));

					// book.setDescription(description);
					JSONObject descriptionObj =  jsonObject.optJSONObject("description");
					if(descriptionObj != null){
						book.setDescription(descriptionObj.optString("value"));
					}
					// book.setPublishedDate(publishedDate);
					JSONObject publishedObj = jsonObject.optJSONObject("created");
					if(publishedObj != null){
						String dateStr = publishedObj.getString("value");
						book.setPublishedDate(LocalDate.parse(dateStr, dateFormat));
					}
					// book.setCoverIds();
					JSONArray coversJSONArr = jsonObject.optJSONArray("covers");
					if(coversJSONArr != null){
						List<String> coverIds = new ArrayList<>();
						for(int i =0;i<coversJSONArr.length();i++){
							coverIds.add(coversJSONArr.getString(i));
						}
						book.setCoverIds(coverIds);
					}

					// book.setCoverIds();
					JSONArray authorJSONArr = jsonObject.optJSONArray("authors");
					if(authorJSONArr != null){
						List<String> authorIds = new ArrayList<>();
						for(int i = 0; i<authorJSONArr.length();i++){
							String authorId = authorJSONArr.getJSONObject(i).getJSONObject("author").getString("key")
									.replace("/authors/", "");
							authorIds.add(authorId);
						}

						book.setAuthorId(authorIds);
						List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
								.map(optionalAuthor -> {
									if(!optionalAuthor.isPresent())
										 return "Unknown Author";
									return optionalAuthor.get().getName();
								}).collect(Collectors.toList());
						book.setAuthorNames(authorNames);
					}
					// 3. Persist Using Repository
					System.out.println("Saving Book " + book.getName() + " ............");
					bookRepository.save(book);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@PostConstruct
	public void start(){
		// Author author = new Author();
		// author.setId("id");
		// author.setName("name");
		// author.setPersonalName("personalName");
		// authorRepository.save(author);
		initWorks();
		initAuthor();
		// System.out.println(worksDumpLocation);
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
