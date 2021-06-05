package by.shipul.jwtappdemo.rest;

import by.shipul.jwtappdemo.model.Developer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperRestControllerV1 {

    private List<Developer> developers = Stream.of(
            new Developer(1L,"User","Userov"),
            new Developer(2L,"Admin","Adminov"),
            new Developer(3L,"Maksim","Maksimov"),
            new Developer(4L,"Andrey","Andreev")
    ).collect(Collectors.toList());

    @GetMapping
    public List<Developer> getAll() {
        return developers;
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable Long id) {
        return developers.stream()
                .filter(developer -> developer.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public Developer create(@RequestBody Developer developer){
        developers.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        developers.removeIf(developer -> developer.getId().equals(id));
    }

}
