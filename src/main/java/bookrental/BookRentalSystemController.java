package bookrental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Convert;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController()
 public class BookRentalSystemController {

 @Autowired
 BookRentalSystemRepository brs;

 @Autowired
 BookListStatusRepository bls;

 @Autowired
 BlurayListStatusRepository rls;

 @PostMapping("/bookRentalSystems/rent")
 public BookRentalSystem rented(@RequestBody BookRentalSystem postBookRental) {

  System.out.println("##### rented!! Id: " + postBookRental.getBookId());
  Optional<BookListStatus> bookListStatusSystemOptional = Optional.empty();
  Optional<BlurayListStatus> blurayListStatusSystemOptional = Optional.empty();
  boolean isBookListStatus = false;
  boolean isBlurayListStatus = false;

  if(postBookRental.getBookId() != null) {
   bookListStatusSystemOptional = bls.findById(postBookRental.getBookId());
   isBookListStatus = true;
  }
  if(postBookRental.getBlurayId() != null){
   blurayListStatusSystemOptional = rls.findById(postBookRental.getBlurayId());
   isBlurayListStatus = true;
  }
  System.out.println("bookListStatusSystemOptional : " + String.valueOf(bookListStatusSystemOptional.isPresent()) );
  System.out.println("blurayListStatusSystemOptional : " + String.valueOf(blurayListStatusSystemOptional.isPresent()) );

  if (isBookListStatus == true && isBlurayListStatus == false) {
   BookListStatus bookListStatus = bookListStatusSystemOptional.get();

   if ("IDLE".equals(bookListStatus.getRentalStatus()))  {
    System.out.println("if in1");
    BookRentalSystem bookRentalSystem = new BookRentalSystem();
    bookRentalSystem.setBookId(postBookRental.getBookId());
    bookRentalSystem.setUserId(postBookRental.getUserId());
    bookRentalSystem.setRentalFee(bookListStatus.getRentalFee());
    bookRentalSystem.setRentalDate(new Date());
    bookRentalSystem.setRentalStatus("REQ_PAY");
    brs.save(bookRentalSystem);
    System.out.println("##### rented!! End!! Id: " + bookRentalSystem.getId());
    return bookRentalSystem;
   } else {
    System.out.println("state is RENT!! : " + postBookRental.getBookId());
    return null;
   }
  }

  else if (isBookListStatus == false && isBlurayListStatus == true) {
   BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();

   if ("IDLE".equals(blurayListStatus.getRentalStatus())) {
    System.out.println("if in2");
    BookRentalSystem bookRentalSystem = new BookRentalSystem();

    bookRentalSystem.setBlurayId(postBookRental.getBlurayId());
    bookRentalSystem.setUserId(postBookRental.getUserId());
    bookRentalSystem.setRentalFee(blurayListStatus.getRentalFee());
    bookRentalSystem.setRentalDate(new Date());
    bookRentalSystem.setRentalStatus("REQ_PAY");
    brs.save(bookRentalSystem);
    System.out.println("##### rented!! End!! Id: " + bookRentalSystem.getId());
    return bookRentalSystem;
   } else {
    System.out.println("state is RENT!! : " + postBookRental.getBookId());
    return null;
   }
  }

  else if (isBookListStatus == true && isBlurayListStatus == true) {
   BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();
   BookListStatus bookListStatus = bookListStatusSystemOptional.get();

   if ("IDLE".equals(bookListStatus.getRentalStatus()) && "IDLE".equals(blurayListStatus.getRentalStatus())) {
    System.out.println("if in3");
    int rentalFee = bookListStatus.getRentalFee() + blurayListStatus.getRentalFee();

    BookRentalSystem bookRentalSystem = new BookRentalSystem();
    bookRentalSystem.setBlurayId(postBookRental.getBlurayId());
    bookRentalSystem.setBookId(postBookRental.getBookId());
    bookRentalSystem.setUserId(postBookRental.getUserId());
    bookRentalSystem.setRentalFee(rentalFee);
    bookRentalSystem.setRentalDate(new Date());
    bookRentalSystem.setRentalStatus("REQ_PAY");
    brs.save(bookRentalSystem);
    System.out.println("##### rented!! End!! Id: " + bookRentalSystem.getId());
    return bookRentalSystem;
   } else {
    System.out.println("state is RENT!! : " + postBookRental.getBookId());
    return null;
   }
  }
  else {
   System.out.println("cant not find ID!! : " + postBookRental.getBookId());
   return null;
  }
 }

 @PostMapping("/bookRentalSystems/return")
 public BookRentalSystem returned(@RequestBody BookRentalSystem postBookRental) {
  Optional<BookRentalSystem> bookRentalSystemOptional = brs.findById(postBookRental.getId());
  if (bookRentalSystemOptional.isPresent()) {
   BookRentalSystem bookRental = bookRentalSystemOptional.get();

   bookRental.setReturnDate(new Date());
   bookRental.setRentalStatus("RETURNED");
   brs.save(bookRental);

   //Optional<BookListStatus> bookListStatusSystemOptional = bls.findById(bookRental.getBookId());
   Optional<BookListStatus> bookListStatusSystemOptional = Optional.empty();
   Optional<BlurayListStatus> blurayListStatusSystemOptional = Optional.empty();
   boolean isBookListStatus = false;
   boolean isBlurayListStatus = false;

   if(postBookRental.getBookId() != null) {
    bookListStatusSystemOptional = bls.findById(postBookRental.getBookId());
    isBookListStatus = true;
   }
   if(postBookRental.getBlurayId() != null){
    blurayListStatusSystemOptional = rls.findById(postBookRental.getBlurayId());
    isBlurayListStatus = true;
   }


   //if (bookListStatusSystemOptional.isPresent()) {
   if (isBookListStatus == true && isBlurayListStatus == false) {
    BookListStatus bookListStatus = bookListStatusSystemOptional.get();
    bookListStatus.setRentalStatus("IDLE");
    bls.save(bookListStatus);
   }
   else if(isBookListStatus == false && isBlurayListStatus == true) {
    BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();
    blurayListStatus.setRentalStatus("IDLE");
    rls.save(blurayListStatus);
   }
   else if(isBookListStatus == true && isBlurayListStatus == true) {
    BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();
    blurayListStatus.setRentalStatus("IDLE");
    BookListStatus bookListStatus = bookListStatusSystemOptional.get();
    bookListStatus.setRentalStatus("IDLE");
    bls.save(bookListStatus);
    rls.save(blurayListStatus);
   }
   else {
    System.out.println("cant not find book ID!! : " + bookRental.getBookId());
   }

   return bookRental;
  }
  System.out.println("cant not find rental ID!! : " + postBookRental.getId());
  return null;
 }

 @PostMapping("/bookRentalSystems/cancel")
 public BookRentalSystem cancelled(@RequestBody BookRentalSystem postBookRental) {

  Optional<BookRentalSystem> bookRentalSystemOptional = brs.findById(postBookRental.getId());
  if (bookRentalSystemOptional.isPresent()) {
   BookRentalSystem bookRental = bookRentalSystemOptional.get();

   bookRental.setReturnDate(new Date());
   bookRental.setRentalStatus("CANCELLED");
   brs.save(bookRental);

   //Optional<BookListStatus> bookListStatusSystemOptional = bls.findById(bookRental.getBookId());
   Optional<BookListStatus> bookListStatusSystemOptional = Optional.empty();
   Optional<BlurayListStatus> blurayListStatusSystemOptional = Optional.empty();
   boolean isBookListStatus = false;
   boolean isBlurayListStatus = false;

   if(postBookRental.getBookId() != null) {
    bookListStatusSystemOptional = bls.findById(postBookRental.getBookId());
    isBookListStatus = true;
   }
   if(postBookRental.getBlurayId() != null){
    blurayListStatusSystemOptional = rls.findById(postBookRental.getBlurayId());
    isBlurayListStatus = true;
   }

   /*if (bookListStatusSystemOptional.isPresent()) {
    BookListStatus bookListStatus = bookListStatusSystemOptional.get();
    bookListStatus.setRentalStatus("IDLE");
    bls.save(bookListStatus);
   }*/

   if (isBookListStatus == true && isBlurayListStatus == false) {
    BookListStatus bookListStatus = bookListStatusSystemOptional.get();
    bookListStatus.setRentalStatus("IDLE");
    bls.save(bookListStatus);
   }
   else if(isBookListStatus == false && isBlurayListStatus == true) {
    BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();
    blurayListStatus.setRentalStatus("IDLE");
    rls.save(blurayListStatus);
   }
   else if(isBookListStatus == true && isBlurayListStatus == true) {
    BlurayListStatus blurayListStatus = blurayListStatusSystemOptional.get();
    blurayListStatus.setRentalStatus("IDLE");
    BookListStatus bookListStatus = bookListStatusSystemOptional.get();
    bookListStatus.setRentalStatus("IDLE");
    bls.save(bookListStatus);
    rls.save(blurayListStatus);
   }

   return bookRental;
  }
  System.out.println("cant not find rental ID!! : " + postBookRental.getId());
  return null;
 }
}