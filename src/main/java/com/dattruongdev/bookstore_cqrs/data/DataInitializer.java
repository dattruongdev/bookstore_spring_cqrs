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
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.ParseException;
import org.bson.types.ObjectId;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationStartedEvent> {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCostRepository bookCostRepository;

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

    private void createAuthorsIfNotExists(List<String> auths, Book book) {
        List<Author> authors = auths.stream().map(author -> {
            Author a = new Author();
            a.setFullName(author);
            return a;
        }).toList();

        book.setAuthors(authorRepository.saveAll(authors));
    }

    private void createCategoriesIfNotExist(List<String> categories, Book book) {
        Set<String> catSet = new HashSet<>(categories);

        List<Category> newCats = catSet.stream().map(cat -> {
            Category category = new Category();
            category.setName(cat);
            return category;
        }).toList();

        book.setCategories(categoryRepository.saveAll(newCats));
    }

    @Transactional
    protected void createBooksIfNotExist() {
        boolean isFeatured = false;
        List<Book> current = bookRepository.findAll();
        List<BookPricing> currentCost = bookCostRepository.findAll();
        List<Author> existingAuthors = authorRepository.findAll();
        List<Category> foundCats = categoryRepository.findAll();
        if (!foundCats.isEmpty()) {
            categoryRepository.deleteAll();
        }

        if (!existingAuthors.isEmpty()) {
            authorRepository.deleteAll();
        }

        if (!currentCost.isEmpty()) {
            bookCostRepository.deleteAll();
        }
        if (!current.isEmpty()) {
            bookRepository.deleteAll();
        }
        List<Book> books = new ArrayList<>();
        try {
            Dta data = readJson();

            List<BookPricing> bookPricings = new ArrayList<>();
            for (int i = 0; i < data.items.size(); i++) {
                Book book = new Book();
                BookJSON bookJSON = data.items.get(i);
                BookPricing bookPricing = new BookPricing();
                ObjectId bookId = ObjectId.get();
                book.setId(bookId);

//              ADD CATEGORIES
                createCategoriesIfNotExist(bookJSON.volumeInfo.categories, book);
//              ADD AUTHORS
                createAuthorsIfNotExists(bookJSON.volumeInfo.authors, book);

                Cost cost = new Cost();
                if(bookJSON.saleInfo.listPrice != null) {
                    cost.setAmount(bookJSON.saleInfo.listPrice.amount);
                    cost.setCurrency(bookJSON.saleInfo.listPrice.currencyCode);
                    bookPricing.setOriginalCost(cost);
                    if(i % 2 == 0) {
                        Random r = new Random();
                        double randomValue = r.nextDouble();
                        cost.setAmount(bookJSON.saleInfo.listPrice.amount * (1 -randomValue));

                        Date dt = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dt);
                        c.add(Calendar.DATE, 1);
                        dt = c.getTime();


                        bookPricing.setWeekDeal(true);
                        bookPricing.changeCost(cost, dt, randomValue);
                    } else {
                        bookPricing.setWeekDeal(false);
                        bookPricing.changeCost(cost, null, 0);
                    }
                } else {
                    cost.setAmount(0);
                    cost.setCurrency("VND");
                    bookPricing.changeCost(cost, null, 0);
                }
                if(bookJSON.volumeInfo.imageLinks != null) {
                    book.setImageUrl(bookJSON.volumeInfo.imageLinks.thumbnail);
                }
                Random r = new Random();
                double randomValue = 3.0 + (5.0 - 3.0) * r.nextDouble();
                book.setTitle(bookJSON.volumeInfo.title);
                book.setFeatured(isFeatured);
                book.setPublisher(bookJSON.volumeInfo.publisher);
                book.setPublishedDate(bookJSON.volumeInfo.publishedDate);
                book.setDescription(bookJSON.volumeInfo.description);

                book.setRating(randomValue);
                book.setBookPricing(bookPricing);
                books.add(book);
                isFeatured = !isFeatured;

                bookPricings.add(bookPricing);
            }

            bookCostRepository.saveAll(bookPricings);
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
