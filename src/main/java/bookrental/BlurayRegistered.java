package bookrental;

public class BlurayRegistered extends AbstractEvent {

    private Long id;
    private String blurayName;
    private Integer rentalFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlurayName() {
        return blurayName;
    }

    public void setBlurayName(String blurayName) {
        this.blurayName = blurayName;
    }

    public Integer getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Integer rentalFee) {
        this.rentalFee = rentalFee;
    }
}
