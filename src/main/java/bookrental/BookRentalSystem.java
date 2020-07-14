package bookrental;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name="BookRentalSystem_table")
public class BookRentalSystem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private Date rentalDate;
    private Date expiredDate;
    private Date returnDate;
    private Integer rentalFee;
    private String rentalStatus;

    @PostPersist
    public void onPostPersist(){

        if ("RENTED".equals(this.getRentalStatus() ) ) {
            Rented rented = new Rented();
            BeanUtils.copyProperties(this, rented);
            rented.publishAfterCommit();
        } else if("RETRUNED".equals(this.getRentalStatus() ) ) {
            Returned returned = new Returned();
            BeanUtils.copyProperties(this, returned);
            returned.publishAfterCommit();
        } else if("CANCELLED".equals(this.getRentalStatus() ) ) {
            Cancelled cancelled = new Cancelled();
            BeanUtils.copyProperties(this, cancelled);
            cancelled.publishAfterCommit();
        }

    }

    @PrePersist
    public void onPrePersist(){
    /*
        Rented rented = new Rented();
        BeanUtils.copyProperties(this, rented);
        rented.publishAfterCommit();
*/
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        bookrental.external.Payment payment = new bookrental.external.Payment();
        // mappings goes here
        payment.setRentalId(this.getId()) ;
        payment.setBookId(this.getBookId());
        payment.setRate(this.getRentalFee());
        payment.setRegDate(this.getRentalDate());
        payment.setPayStatus(this.getRentalStatus());
        BookrentalsystemApplication.applicationContext.getBean(bookrental.external.PaymentService.class)
            .pay(payment);


    }

    @PreRemove
    public void onPreRemove(){
        Cancelled cancelled = new Cancelled();
        BeanUtils.copyProperties(this, cancelled);
        cancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        RestTemplate restTemplate = BookrentalsystemApplication.applicationContext.getBean(RestTemplate.class);
        String productUrl = "localhost:8084/payments/search/findByRentalIdAndBookId rentalId==" + this.getId() + " bookId==" + this.getBookId() ;

        ResponseEntity<String> paymentEntity = restTemplate.getForEntity( productUrl, String.class);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(paymentEntity.getBody()).getAsJsonObject();

        Long id ;
        id = jsonObject.get("id").getAsLong();

        bookrental.external.Payment payment = new bookrental.external.Payment();
        // mappings goes here
        payment.setId(id);
        payment.setRentalId(this.getId()) ;
        payment.setBookId(this.getBookId());
        payment.setRate(this.getRentalFee());
        payment.setRegDate(this.getRentalDate());
        payment.setPayStatus("CANCELLED");
        BookrentalsystemApplication.applicationContext.getBean(bookrental.external.PaymentService.class)
            .refund(payment);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }
    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    public Integer getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Integer rentalFee) {
        this.rentalFee = rentalFee;
    }
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }




}
