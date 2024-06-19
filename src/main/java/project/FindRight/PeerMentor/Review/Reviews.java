    package project.FindRight.PeerMentor.Review;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import project.FindRight.PeerMentor.PeerMentor;
    import project.FindRight.Users.User;

    @Entity
    @Table(name="Reviews")
    public class Reviews {
        @GeneratedValue(strategy= GenerationType.AUTO)
        @Id
        private Integer id;

        @Column(name="Stars")
        private Integer stars;

        @Column(name="Comment")
        private String comment;

        @ManyToOne
        @JoinColumn(name="peerMentor_id")
        @JsonIgnore
        private PeerMentor peerMentor;

        @ManyToOne
        @JoinColumn(name="user_id")
        @JsonIgnore
        private User user;

        public Reviews (){}

        public Reviews(Integer stars, String comment, PeerMentor peerMentor) {
            if (stars<1 || stars> 5){
                throw new IllegalArgumentException("Rating must be between 1-5");
            }
            this.stars = stars;
            this.comment = comment;
            this.peerMentor = peerMentor;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getStars() {
            return stars;
        }

        public void setStars(Integer stars) {
            if (stars < 1 || stars > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
            this.stars = stars;

            // After setting the stars, update the average rating of the associated peer mentor
            if (peerMentor != null) {
                peerMentor.updateAverageRating();
            }
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public PeerMentor getPeerMentor() {
            return peerMentor;
        }

        public void setPeerMentor(PeerMentor peerMentor) {
            this.peerMentor = peerMentor;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User users) {
            this.user = users;
        }
    }
