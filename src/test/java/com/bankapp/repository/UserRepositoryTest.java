package com.bankapp.repository;

import com.bankapp.dto.request.UserRequestDTO;
import com.bankapp.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.DERBY)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;


    //DATA FOR TESTING
    private UserRequestDTO userRequestDTO;
    @BeforeEach
    void setUp() {
        userRequestDTO.setId(5L);
        userRequestDTO.setGender("male");
        userRequestDTO.setEmail("sundaypetersp12@gmail.com");
        userRequestDTO.setAddress("6 olaside close maryland lagos");
        userRequestDTO.setAlternativePhoneNumber("08186707807");
        userRequestDTO.setStateOfOrigin("Benue");
        userRequestDTO.setLastName("peter");
        userRequestDTO.setFirstName("sunday");
        userRequestDTO.setOtherName("ochefije");
        userRequestDTO.setAccountBalance(BigDecimal.valueOf(200_300_400));
        userRequestDTO.setAccountNumber("2000000000");
        userRequestDTO.setPhoneNumber("08186707807");
        userRequestDTO.setStatus("Active");
        userRequestDTO.getCreatedAt();
        userRequestDTO.getModifiedAt();

    }

    @Test
    public void GivenUserRequest_WhenSaved_ThenReturnUserRequestDTO(){
        //GIVEN
        UserRequestDTO userRequestDTOGiven = userRequestDTO;
        //WHEN
        User actual = userRepository.save(new User(
                userRequestDTO.getId(),
        userRequestDTO.getGender(),
        userRequestDTO.getEmail(),
        userRequestDTO.getAddress(),
        userRequestDTO.getAlternativePhoneNumber(),
        userRequestDTO.getStateOfOrigin(),
        userRequestDTO.getLastName(),
        userRequestDTO.getFirstName(),
        userRequestDTO.getOtherName(),
        userRequestDTO.getAccountBalance(),
        userRequestDTO.getAccountNumber(),
        userRequestDTO.getPhoneNumber(),
        userRequestDTO.getStatus(),
        userRequestDTO.getCreatedAt(),
        userRequestDTO.getModifiedAt()
        ));

        //ASSERT
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("sunday", actual.getFirstName());

    }



//    @Test
//    void existsByEmail() {
//    }
//
//    @Test
//    void existsByAccountNumber() {
//    }
//
//    @Test
//    void findByAccountNumber() {
//    }
}