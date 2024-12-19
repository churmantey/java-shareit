package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTests {

    private final EntityManager em;
    private final ItemServiceImpl service;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestServiceImpl requestService;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private UserDto userDto;
    private UserDto userDto2;

    private ItemRequestDto requestDto;

    @BeforeEach
    public void setUp() {

        userDto = new UserDto(1L, "Пётр Иванов", "some@email.com");
        userDto = userService.createUser(userDto);

        userDto2 = new UserDto(2L, "Иван Петров", "petrov@email.com");
        userDto2 = userService.createUser(userDto2);

        itemDto1 = new ItemDto();
        itemDto1.setName("first item");
        itemDto1.setDescription("first description");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(userDto.getId());

        itemDto2 = new ItemDto();
        itemDto2.setName("second item");
        itemDto2.setDescription("second description");
        itemDto2.setAvailable(true);
        itemDto2.setOwner(userDto.getId());

        NewItemRequestDto newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("request description");

        requestDto = requestService.createItemRequest(userDto2.getId(), newRequestDto);

    }

    @Test
    public void getItemsByOwnerTest() {

        List<ItemWithBookingsDto> userItems = service.getItemsByOwner(userDto.getId());
        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(0));

        ItemDto storedItemDto1 = service.createItem(itemDto1);
        ItemDto storedItemDto2 = service.createItem(itemDto2);

        userItems = service.getItemsByOwner(userDto.getId());

        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(2));
        assertThat(userItems.get(0).getId(), equalTo(storedItemDto1.getId()));
        assertThat(userItems.get(0).getName(), equalTo(storedItemDto1.getName()));
        assertThat(userItems.get(1).getId(), equalTo(storedItemDto2.getId()));
        assertThat(userItems.get(1).getName(), equalTo(storedItemDto2.getName()));
    }

    @Test
    public void getItemTest() {
        itemDto1 = service.createItem(itemDto1);

        ItemWithBookingsDto retrievedDto = service.getItem(itemDto1.getId());

        assertThat(retrievedDto.getName(), equalTo(itemDto1.getName()));
        assertThat(retrievedDto.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(retrievedDto.getOwner(), equalTo(itemDto1.getOwner()));

    }

    @Test
    public void createItemTest() {
        itemDto1 = service.createItem(itemDto1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where id = :id", Item.class);
        Item item = query.setParameter("id", itemDto1.getId())
                .getSingleResult();
        assertThat(item, notNullValue());
        assertThat(item.getName(), equalTo(itemDto1.getName()));
        assertThat(item.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(item.getOwner(), equalTo(itemDto1.getOwner()));


    }

    @Test
    public void createItemWithEmptyName() {
        assertThrows(ValidationException.class, () -> service.createItem(null));
        itemDto2.setName("");
        assertThrows(ValidationException.class, () -> service.createItem(itemDto2));
    }

    @Test
    public void createItemWithEmptyDescription() {
        itemDto2.setDescription("");
        assertThrows(ValidationException.class, () -> service.createItem(itemDto2));
    }

    @Test
    public void createNullItem() {
        itemDto2.setAvailable(null);
        assertThrows(ValidationException.class, () -> service.createItem(itemDto2));
    }

    @Test
    public void createItemWithInvalidRequest() {
        itemDto2.setRequestId(999L);
        assertThrows(ValidationException.class, () -> service.createItem(itemDto2));
    }

    @Test
    public void createItemNonexistentOwnerTest() {
        itemDto1.setOwner(9999L);
        assertThrows(NotFoundException.class, () -> service.createItem(itemDto1));
    }

    @Test
    public void updateItemTest() {
        itemDto1 = service.createItem(itemDto1);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("updated item");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setOwner(itemDto1.getOwner());
        updatedItemDto.setId(itemDto1.getId());
        updatedItemDto.setRequestId(requestDto.getId());

        service.updateItem(updatedItemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where id = :id", Item.class);
        Item item = query.setParameter("id", itemDto1.getId())
                .getSingleResult();
        assertThat(item, notNullValue());
        assertThat(item.getName(), equalTo(updatedItemDto.getName()));
        assertThat(item.getDescription(), equalTo(updatedItemDto.getDescription()));
    }

    @Test
    public void updateItemInvalidRequestTest() {
        itemDto1 = service.createItem(itemDto1);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("updated item");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setOwner(itemDto1.getOwner());
        updatedItemDto.setId(itemDto1.getId());
        updatedItemDto.setRequestId(9999L);

        assertThrows(ValidationException.class, () -> service.updateItem(updatedItemDto));
    }

    @Test
    public void updateItemNoAccessOwnerTest() {
        itemDto1 = service.createItem(itemDto1);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("updated item");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setOwner(userDto2.getId());
        updatedItemDto.setId(itemDto1.getId());

        assertThrows(AccessException.class, () -> service.updateItem(updatedItemDto));
    }


    @Test
    public void findAvailableItemsByTextTest() {

        ItemDto iwb1 = service.createItem(itemDto1);
        ItemDto iwb2 = service.createItem(itemDto2);

        List<ItemDto> retrievedList = service.findAvailableItemsByText("item");

        assertThat(retrievedList.size(), equalTo(2));
        assertThat(retrievedList.get(0).getName(), equalTo(itemDto1.getName()));
        assertThat(retrievedList.get(0).getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(retrievedList.get(1).getName(), equalTo(itemDto2.getName()));
        assertThat(retrievedList.get(1).getDescription(), equalTo(itemDto2.getDescription()));
    }

    @Test
    public void deleteItemTest() {
        ItemDto itemDto = service.createItem(itemDto1);
        service.deleteItemById(itemDto.getId());
        Assertions.assertThrows(NotFoundException.class, () -> service.getItem(itemDto.getId()));
    }

    @Test
    public void addCommentTest() {
        ItemDto itemDto = service.createItem(itemDto1);

        UserDto userDto2 = userService.createUser(
                new UserDto(null, "Иван Петров", "some.petrov@email.com")
        );

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(itemDto.getId());
        newBookingDto.setStart(LocalDateTime.now().minusDays(2));
        newBookingDto.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto bookingDto = bookingService.createBooking(newBookingDto, userDto2.getId());

        bookingService.approveBooking(bookingDto.getId(), userDto.getId(), true);

        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("test comment");
        newCommentDto.setAuthorName("somebody");
        service.addComment(userDto2.getId(), itemDto.getId(), newCommentDto);

        ItemWithBookingsDto itemWithBookingsDto = service.getItem(itemDto.getId());

        assertThat(itemWithBookingsDto, notNullValue());
        assertThat(itemWithBookingsDto.getId(), equalTo(itemDto.getId()));
        assertThat(itemWithBookingsDto.getComments().size(), equalTo(1));
        assertThat(itemWithBookingsDto.getComments().get(0).getText(), equalTo(newCommentDto.getText()));

        assertThrows(ValidationException.class,
                () -> service.addComment(userDto.getId(), itemDto.getId(), newCommentDto));
    }

}
