package com.dattruongdev.bookstore_cqrs.data;

import com.dattruongdev.bookstore_cqrs.core.auth.domain.Role;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.RoleRepository;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.User;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.UserRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationStartedEvent> {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        createRoleIfNotExist();
        createAdminIfNotExist();
        createBooksIfNotExist();
    }

    private void createRoleIfNotExist() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            Role member = new Role();
            member.setRoleName("ROLE_USER");
            roleRepository.save(member);

            Role admin = new Role();
            admin.setRoleName("ROLE_ADMIN");
            roleRepository.save(admin);
        }
    }

    private List<Category> createCategoriesIfNotExist(Dta data) {
        Set<String> categories = data.items.stream().map(bookJSON -> bookJSON.volumeInfo.categories).flatMap(List::stream).collect(Collectors.toSet());
        List<Category> foundCats = categoryRepository.findByNameIn(categories.stream().toList());
        List<Category> cats =  categoryRepository.saveAll(foundCats);

        return cats;
    }

    @Transactional
    protected void createBooksIfNotExist() {
        List<Book> current = bookRepository.findAll();
        if (!current.isEmpty()) {
            return;
        }
        List<Book> books = new ArrayList<>();
        try {
            Dta data = readJson();
            List<Category> categories = createCategoriesIfNotExist(data);

            for (BookJSON bookJSON : data.items) {
                Book book = new Book();
                Cost cost = new Cost();

                List<Category> cats = categories.stream().filter(cat -> bookJSON.volumeInfo.categories.contains(cat.getName())).toList();

                if (bookJSON.saleInfo.listPrice != null) {
                    cost.setCurrency(bookJSON.saleInfo.listPrice.currencyCode);
                    cost.setAmount(bookJSON.saleInfo.listPrice.amount);
                }

                book.setTitle(bookJSON.volumeInfo.title);
                book.setAuthors(bookJSON.volumeInfo.authors);
                book.setCost(cost);
                book.setDescription(bookJSON.volumeInfo.description);
                book.setPublisher(bookJSON.volumeInfo.publisher);
                book.setPublishedDate(bookJSON.volumeInfo.publishedDate);
                if (bookJSON.volumeInfo.imageLinks != null) {
                    book.setImageUrl(StringUtils.isEmpty(bookJSON.volumeInfo.imageLinks.smallThumbnail) ? bookJSON.volumeInfo.imageLinks.thumbnail : bookJSON.volumeInfo.imageLinks.smallThumbnail);
                }

                book.setCategories(cats);
                books.add(book);
            }

            bookRepository.saveAll(books);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAdminIfNotExist() {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN");

        if (role == null) {
            role = new Role();
            role.setRoleName("ROLE_ADMIN");
            role = roleRepository.save(role);
        }

        User user = userRepository.findByUsername("admin");
        if (user == null) {
            user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRoles(List.of(role));
            userRepository.save(user);
        }
    }

    private <T> T fromJSON(final TypeReference<T> type,
                           final String jsonPacket) throws JsonProcessingException {

        T data = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            data = mapper.readValue(jsonPacket, type);
            System.out.println();
        } catch (Exception e) {
            // Handle the problem
            throw e;
        }
        return (T) data;
    }

    private Dta readJson() throws IOException, ParseException {
        String path = new File("").getAbsolutePath();
        InputStream is = this.getClass().getResourceAsStream("/data.json");
        StringBuilder jsonText = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";
        while ((line = br.readLine()) != null) {
            jsonText.append(line).append("\n");
        }
        is.close();
        br.close();

        return fromJSON(new TypeReference<Dta>() {
        }, jsonText.toString());
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Dta {
    public String kind;
    public int totalItems;
    public List<BookJSON> items;
}

class VolumeInfo {
    public String title;
    public List<String> authors;
    public List<String> categories;
    public String publisher;
    public String publishedDate;
    public String description;
    public ImageLink imageLinks;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BookJSON {
    public String id;
    public VolumeInfo volumeInfo;
    public SaleInfo saleInfo;


}

class SaleInfo {
    public String country;
    public ListPrice listPrice;
}

class ListPrice {
    public double amount;
    public String currencyCode;
}

class ImageLink {
    public String thumbnail;
    public String smallThumbnail;
}
