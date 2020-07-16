package bookrental;

import bookrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    BookRentalSystemRepository bookRentalRepo;

    @Autowired
    BookListStatusRepository bookStatusRepo;
    @Autowired
    BlurayListStatusRepository blurayStatusRepo;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRegistered_ChangeBookStatus(@Payload BookRegistered bookRegistered){

        if(bookRegistered.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + bookRegistered.toJson());

            BookListStatus bookStatus = new BookListStatus();
            bookStatus.setBookName(bookRegistered.getBookName());
            bookStatus.setRentalFee(bookRegistered.getRentalFee());
            bookStatus.setId(bookRegistered.getId());
            bookStatus.setRentalStatus("IDLE");

            bookStatusRepo.save(bookStatus);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBlurayRegistered_ChangeBlurayStatus(@Payload BlurayRegistered blurayRegistered){

        if(blurayRegistered.isMe()){
            System.out.println("##### listener ChangeBlurayStatus : " + blurayRegistered.toJson());

            BlurayListStatus blurayStatus = new BlurayListStatus();
            blurayStatus.setBlurayName((blurayRegistered.getBlurayName()));
            blurayStatus.setRentalFee(blurayRegistered.getRentalFee());
            blurayStatus.setId(blurayRegistered.getId());
            blurayStatus.setRentalStatus("IDLE");
            System.out.println("Set Complete");

            blurayStatusRepo.save(blurayStatus);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_ChangeBookStatus(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + paid.toJson());

            Optional<BookRentalSystem> bookRentalSystemOptional = bookRentalRepo.findById(paid.getRentalId());
            if( bookRentalSystemOptional.isPresent() ) {
                BookRentalSystem bookRental = bookRentalSystemOptional.get();
                bookRental.setRentalStatus("RENTED");

                System.out.println("RENTED. book Id : " + bookRental.getBookId());

                bookRentalRepo.save(bookRental);


                Optional<BookListStatus> bookListStatusOptional = bookStatusRepo.findById(bookRental.getBookId());
                if( bookListStatusOptional.isPresent() ){
                    BookListStatus bookStatus = bookListStatusOptional.get();

                    bookStatus.setRentalStatus("RENTED");

                    bookStatusRepo.save(bookStatus);
                }
                else {
                    System.out.println("wheneverPaid_ChangeBookStatus, book Id : " + bookRental.getBookId());
                }
                Optional<BlurayListStatus> blurayListStatusOptional = blurayStatusRepo.findById(bookRental.getBlurayId());
                if(blurayListStatusOptional.isPresent()){
                    BlurayListStatus blurayStatus = blurayListStatusOptional.get();

                    blurayStatus.setRentalStatus("RENTED");

                    blurayStatusRepo.save(blurayStatus);
                }
            }
            else {
                System.out.println("RENTED. rental Id : " + paid.getRentalId());
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefunded_ChangeBookStatus(@Payload Refunded refunded){

        if(refunded.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + refunded.toJson());

            Optional<BookRentalSystem> bookRentalSystemOptional = bookRentalRepo.findById(refunded.getRentalId());
            if( bookRentalSystemOptional.isPresent() ) {
                BookRentalSystem bookRental = bookRentalSystemOptional.get();

                Optional<BookListStatus> bookListStatusOptional = bookStatusRepo.findById(bookRental.getBookId());
                if( bookListStatusOptional.isPresent() ){
                    BookListStatus bookStatus = bookListStatusOptional.get();

                    bookStatus.setRentalStatus("IDLE");

                    bookStatusRepo.save(bookStatus);
                }

                Optional<BlurayListStatus> blurayListStatusOptional = blurayStatusRepo.findById(bookRental.getBlurayId());
                if(blurayListStatusOptional.isPresent()){
                    BlurayListStatus blurayStatus = blurayListStatusOptional.get();

                    blurayStatus.setRentalStatus("IDLE");

                    blurayStatusRepo.save(blurayStatus);
                }
            }
        }
    }

}
