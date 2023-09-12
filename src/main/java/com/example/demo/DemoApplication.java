package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

class Coffee {
	private final String id;
	private String name;

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

@RestController
@RequestMapping("/coffees") // 이 Annotation을 통해 각 메소드마다 반복되는 URI를 통합할 수 있다.
class RestApiDemoController {
	private List<Coffee> coffees = new ArrayList<>();

	public RestApiDemoController() {
		coffees.addAll(List.of(
						new Coffee("Cafe Cereza"),
						new Coffee("Cafe Ganador"),
						new Coffee("Cafe Lareno"),
						new Coffee("Cafe Tres Pontas")
		));
	}

	@GetMapping
	Iterable<Coffee> getCoffees() { //Iterable<Coffee>는 getCoffees() 메소드의 반환 타입을 나타냄. Iterable은 반복 가능한 Java의 인터페이스.
		return coffees;
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) { //Optional은 생략 가능한 값을 나타내는 Java 클래스.
		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c); //Optional.of()는 매개변수를 Optional 클래스로 감싸는 역할을 함.
			}
		}

		return Optional.empty();
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		coffees.add(coffee);
		return coffee;
	}

	@PutMapping("/{id}") // Put 메서드는 상태코드 반환이 필수. 그래서 상태 코드를 포함하는 ResponseEntity 클래스 객체를 반환한다.
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		int coffeeIndex = -1;

		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}

		return (coffeeIndex == -1) ? 
				new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) : 
				new ResponseEntity<>(coffee, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffees.removeIf(c -> c.getId().equals(id)); // removeIf 메서드는 List의 각 요소에 대해 Predicate 함수를 호출하여 조건을 검사함. 조건이 True인 요소를 발견할시 이를 삭제함.
													 // Predicate란 Java에서 함수형 프로그래밍을 지원하는 인터페이스. 하나의 인자를 받아서 true, 또는 false를 반환함.
	}

}

