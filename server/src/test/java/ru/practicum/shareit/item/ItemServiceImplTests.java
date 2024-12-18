package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTests {

    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private UserDto userDto;


    @BeforeEach
    public void setUp() {

        userDto = new UserDto(1L, "Пётр Иванов", "some@email.com");
        userDto = userService.createUser(userDto);

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
    }

    @Test
    public void getItemsByOwnerTest() {

        TypedQuery<Item> query = em.createQuery("Select i from Item i where owner = :id", Item.class);
        List<Item> userItems = query.setParameter("id", userDto.getId())
                .getResultList();
        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(0));

        ItemDto storedItemDto1 = service.createItem(itemDto1);
        ItemDto storedItemDto2 = service.createItem(itemDto2);

        userItems = query.setParameter("id", userDto.getId())
                .getResultList();

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
    public void updateItemTest() {
        itemDto1 = service.createItem(itemDto1);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("updated item");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setOwner(itemDto1.getOwner());
        updatedItemDto.setId(itemDto1.getId());

        service.updateItem(updatedItemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where id = :id", Item.class);
        Item item = query.setParameter("id", itemDto1.getId())
                .getSingleResult();
        assertThat(item, notNullValue());
        assertThat(item.getName(), equalTo(updatedItemDto.getName()));
        assertThat(item.getDescription(), equalTo(updatedItemDto.getDescription()));
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

}
