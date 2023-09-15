package com.example.demo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@SpringBootApplication
@ConfigurationPropertiesScan // ConfigurationProperties 클래스를 스캔하기 위한 어노테이션
public class DemoApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean // Droid 클래스를 스프링 빈으로 인스턴스화하는 어노테이션
	@ConfigurationProperties(prefix = "droid") // Droid 클래스의 멤버 변수를 지정하기 위한 어노테이션
	Droid createDroid() {
		return new Droid();
	}


}

@RestController
@RequestMapping("/droid")
class DroidController {
	private final Droid droid;

	public DroidController(Droid droid) {
		this.droid = droid;
	}

	@GetMapping
	Droid getDroid() {
		return droid;
	}

}

@Entity 
class Coffee {
	@Id 
	private String id;
	private String name;

	// No args Constructor(기본 생성자) 메서드. 기본 생성자란 파라미터가 없는 생성자를 말함.
	// JPA를 사용할때에는 기본 생성자(no-argument)가 필요함. 
	public Coffee() {
	}

	// Constructor 메서드 : 객체를 생성하는 역할을 함. 생성된 객체를 반환. new 키워드와 함께 클래스를 생성할때 자동을 호출됨.
	// 이름이 Class의 이름과 동일함. 반환값을 명시하지 않음에도 void를 선언하지 않음.
	public Coffee(String id, String name) {
		this.id = id; // this는 클래스 내부의 인스턴스 변수를 뜻함
		this.name = name;
	}

	// Constructor 메서드 
	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	// Setter 메서드
	public void setId(String id) {
		this.id = id;
	}

	// Getter 메서드
	public String getId() {
		return id;
	}

	// Getter 메서드
	public String getName() {
		return name;
	}

	// Setter 메서드 : 객체의 상태를 변경하는 역할을 함. void 메서드는 Constructor 메서드와 동일하게 반환값을 명시하지 않지만, 이름이 Class 이름과 다른 것으로 구분 가능함.
	public void setName(String name) {
		this.name = name;
	}
}

interface CoffeeRepository extends CrudRepository<Coffee, String> {}

@RestController
@RequestMapping("/coffees") // 이 Annotation을 통해 각 메소드마다 반복되는 URI를 통합할 수 있다.
class RestApiDemoController {

	private final CoffeeRepository coffeeRepository;
	
	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@GetMapping
	Iterable<Coffee> getCoffees() { //Iterable<Coffee>는 getCoffees() 메소드의 반환 타입을 나타냄. Iterable은 반복 가능한 Java의 인터페이스.
		return coffeeRepository.findAll();
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) { //Optional은 생략 가능한 값을 나타내는 Java 클래스.
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}

	@PutMapping("/{id}") // Put 메서드는 상태코드 반환이 필수. 그래서 상태 코드를 포함하는 ResponseEntity 클래스 객체를 반환한다.
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		return (coffeeRepository.existsById(id))
			   	? new ResponseEntity<>(coffeeRepository.save(coffee),HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee),HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
		// coffees.removeIf(c -> c.getId().equals(id)); // removeIf 메서드는 List의 각 요소에 대해 Predicate 함수를 호출하여 조건을 검사함. 조건이 True인 요소를 발견할시 이를 삭제함.
		// 											 // Predicate란 Java에서 함수형 프로그래밍을 지원하는 인터페이스. 하나의 인자를 받아서 true, 또는 false를 반환함.
	}
}

class Droid {
	private String id, description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

@ConfigurationProperties(prefix = "greeting") // ConfigurationProperties 어노테이션은 클래스 내의 변수 값을 환경 속성, 명령줄 매개변수 등으로 지정할 수 있도록 한다.
class Greeting {
	private String name;
	private String coffee;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}
}

@RestController
@RequestMapping("/greeting")
class GreetingController {
	private final Greeting greeting;

	public GreetingController(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping
	String getGreeting() {
		return greeting.getName();
	}

	@GetMapping("/coffee")
	String getNameAndCoffee() {
		return greeting.getCoffee();
	}
	
}

// 데이터 생성 로직의 분리를 위해 별도 class를 생성.
@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;
	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(
			new Coffee("Cafe Cereza"),
			new Coffee("Cafe Ganador"),
			new Coffee("Cafe Lareno"),
			new Coffee("Cafe Tres Pontas")
		));
	}
}



