package klu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klu.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
 
	
}
