package com.dattruongdev.bookstore_cqrs.data;

import com.dattruongdev.bookstore_cqrs.core.auth.domain.Role;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.RoleRepository;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.User;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.UserRepository;
import com.dattruongdev.bookstore_cqrs.core.catalog.domain.*;
import com.dattruongdev.bookstore_cqrs.core.transaction.domain.BookSaleRepository;
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
import java.time.LocalDateTime;
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
    private final ReviewRepository reviewRepository;
    private final CopyRepository copyRepository;
    private final BookSaleRepository bookSaleRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        createRoleIfNotExist();
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
        Set<String> currentAuthors = new HashSet<>(auths);
        List<Author> authrs = authorRepository.findByFullNameIn(currentAuthors.stream().toList());
        if (!authrs.isEmpty()) {
            book.setAuthors(authrs);
            return;
        }

        List<Author> authors = currentAuthors.stream().map(author -> {
            Author a = new Author();
            a.setFullName(author);
            return a;
        }).toList();

        book.setAuthors(authorRepository.saveAll(authors));
    }

    private void createCategoriesIfNotExist(List<String> categories, Book book) {

        Set<String> catSet = new HashSet<>();
        categories.forEach(cat -> {
            Category category = categoryRepository.findByName(cat);
            if (category == null) {
                catSet.add(cat);
            }
        });

        List<Category> newCats = catSet.stream().map(cat -> {
            Category category = new Category();
            category.setName(cat);
            return category;
        }).toList();

        List<Category> cats = categoryRepository.saveAll(newCats);

        if (!cats.isEmpty()) {
            book.setCategories(cats);
        } else {
            List<Category> originalCats = categoryRepository.findAllByNameIn(categories);
            book.setCategories(originalCats);
        }
    }

    private void createUsersIfNotExist() {
        Role role = roleRepository.findByRoleName("ROLE_USER");
        List<User> users = new ArrayList<>();

        if (role == null) {
            role = new Role();
            role.setRoleName("ROLE_USER");
            role = roleRepository.save(role);
        }

        for(int i = 0; i < 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword(passwordEncoder.encode("user" + i));
            user.setRoles(List.of(role));
            user.setEmail("user" + i + "@gmail.com");
            user.setFirstName("User");
            user.setLastName(i + "");

            users.add(user);
        }

        userRepository.saveAll(users);
    }

    private void createReviewsIfNotExist(Book book) {
        List<User> users = userRepository.findAll();

        List<Review> reviews = new ArrayList<>();

        double sum = 0;

        for (User usr : users) {
            Review review = new Review();
            review.setBookId(book.getId());
            review.setContent(String.join(" ", generateRandomWords(20)));
            review.setEmail(usr.getEmail());
            review.setUsername(usr.getFirstName() + " " + usr.getLastName());
            review.setRating((new Random()).nextInt(5 - 3 + 1) + 3);
            review.setCreatedAt(new Date());
            review.setUpdatedAt(new Date());
            reviews.add(review);
            sum += review.getRating() / (double) users.size();
        }

        book.setRating(sum);

        reviewRepository.saveAll(reviews);
    }

    private String[] generateRandomWords(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
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
//        delete all reviews
        reviewRepository.deleteAll();

        //        delete all users
        userRepository.deleteAll();

        // delete all copies
        copyRepository.deleteAll();
        //delete booksale
        bookSaleRepository.deleteAll();

        // createUser and admin
        createAdminIfNotExist();
        createUsersIfNotExist();

        List<Book> books = new ArrayList<>();
        List<Copy> copies = new ArrayList<>();
        try {
            Dta data = readJson();

            List<BookPricing> bookPricings = new ArrayList<>();
            for (int i = 0; i < data.items.size(); i++) {
                Book book = new Book();
                BookJSON bookJSON = data.items.get(i);
                BookPricing bookPricing = new BookPricing();
                ObjectId bookId = ObjectId.get();
                book.setId(bookId);
                book.setNumberOfPages(bookJSON.volumeInfo.pageCount);
                Integer noCopies = copyRepository.findCountCopiesAvailableByBookId(book.getId());
                int numOfCopies = noCopies == null ? 0 : noCopies;
                book.setNumberOfCopies(numOfCopies);

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
                book.setTitle(bookJSON.volumeInfo.title);
                book.setFeatured(isFeatured);
                book.setPublisher(bookJSON.volumeInfo.publisher);
                book.setPublishedDate(bookJSON.volumeInfo.publishedDate);
                book.setDescription(bookJSON.volumeInfo.description);

                ObjectId bookPricingId = ObjectId.get();
                bookPricing.setId(bookPricingId);

                book.setBookPricing(bookPricingId);

                for(int j = 0; j < 3; j++) {
                    Copy copy = new Copy();
                    copy.setId(ObjectId.get());
                    copy.setCreatedAt(LocalDateTime.now().toString());
                    copy.setUpdatedAt(LocalDateTime.now().toString());
                    copy.setBookId(bookId);
                    copy.setAvailable(true);
                    copies.add(copy);
                    book.addCopy(copy.getId());
                }

                books.add(book);
                isFeatured = !isFeatured;

                bookPricings.add(bookPricing);
            }

            copyRepository.saveAll(copies);
            bookCostRepository.saveAll(bookPricings);
            books = bookRepository.saveAll(books);

            for (Book book : books) {
                createReviewsIfNotExist(book);
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
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setEmail("admin@gmail.com");
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
    public int pageCount;
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
