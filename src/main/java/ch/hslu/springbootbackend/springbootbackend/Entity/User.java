package ch.hslu.springbootbackend.springbootbackend.Entity;

import ch.hslu.springbootbackend.springbootbackend.Entity.Sets.ExamSet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;
@Lazy
@Entity
@Table(	name = "users",
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email")
		})
public class User extends RepresentationModel<User> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(targetEntity = Question.class, cascade = CascadeType.DETACH)
	private List<Question> createdQuestions = new ArrayList<>();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(targetEntity = ExamSet.class, cascade = CascadeType.DETACH)
	private List<ExamSet> createdExamSets = new ArrayList<>();

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(targetEntity = Statistic.class, cascade = CascadeType.ALL)
	private Set<Statistic> statistics;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(targetEntity = ExamResult.class, cascade = CascadeType.ALL)
	private Set<ExamResult> examResults;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "schoolClass_user",
			joinColumns = @JoinColumn(name = "userId"),
			inverseJoinColumns = @JoinColumn(name = "schoolClassId"))
	private List<SchoolClass> inSchoolClasses = new LinkedList<>();


	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(targetEntity = Profession.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_profession", nullable = true)
	private Profession profession;

	@JsonIgnore
	private String resetPasswordToken;


	public User() {
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public User(String username, String email, String password, Profession profession) {

	}

	public User(String username, String email, String password, Profession profession, Set<Role> role, List<SchoolClass> schoolClasses) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.profession = profession;
		this.roles = role;
		this.inSchoolClasses = schoolClasses;
	}

	@PostPersist
	private void assignFKs(){
		for(int i =0; i < getInSchoolClasses().size(); i++){
			getInSchoolClasses().get(i).insertUser(this);
		}
		if(this.profession != null){
			this.profession.getUsersInProfession().add(this);
		}
	}


	public void insertSchoolClass(SchoolClass schoolClass){
		this.getInSchoolClasses().add(schoolClass);
	}
	public void removeSchoolClass(SchoolClass schoolClass){
		this.getInSchoolClasses().remove(schoolClass);
	}

	public Set<Statistic> getStatistics() {
		return statistics;
	}

	public void setStatistics(Set<Statistic> statistics) {
		this.statistics = statistics;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<Question> getCreatedQuestions() {
		return createdQuestions;
	}

	public void setCreatedQuestions(List<Question> createdQuestions) {
		this.createdQuestions = createdQuestions;
	}

	public List<SchoolClass> getInSchoolClasses() {
		return inSchoolClasses;
	}

	public void setInSchoolClasses(List<SchoolClass> inSchoolClasses) {
		this.inSchoolClasses = inSchoolClasses;
	}


	public List<ExamSet> getCreatedExamSets() {
		return createdExamSets;
	}

	public void setCreatedExamSets(List<ExamSet> createdExamSets) {
		this.createdExamSets = createdExamSets;
	}


	public Set<ExamResult> getExamResults() {
		return examResults;
	}

	public void setExamResults(Set<ExamResult> examResults) {
		this.examResults = examResults;
	}


	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}


	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

}
