package pl.factorymethod.rada.contributions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.contributions.dto.CreateContributionRequest;
import pl.factorymethod.rada.contributions.dto.ContributionResponse;
import pl.factorymethod.rada.contributions.repository.ContributionRepository;
import pl.factorymethod.rada.model.Contribution;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.Target;
import pl.factorymethod.rada.model.TargetStudent;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.targets.repository.TargetRepository;
import pl.factorymethod.rada.targets.repository.TargetStudentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

	private final ContributionRepository contributionRepository;
	private final TargetRepository targetRepository;
	private final StudentRepository studentRepository;
	private final TargetStudentRepository targetStudentRepository;

	@Transactional
	public void createContribution(CreateContributionRequest request) {
		UUID targetPublicId = UUID.fromString(request.getTargetId());
		UUID studentPublicId = UUID.fromString(request.getStudentId());

		// Find target and student
		Target target = targetRepository.findByPublicId(targetPublicId)
				.orElseThrow(() -> new RuntimeException("Target not found: " + request.getTargetId()));

		Student student = studentRepository.findByPublicId(studentPublicId)
				.orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

		// Verify that fee is calculated (frozen) for this student-target combination
		TargetStudent targetStudent = targetStudentRepository.findById(
				new pl.factorymethod.rada.model.TargetStudentId() {
					{
						setTargetId(target.getId());
						setStudentId(student.getId());
					}
				}).orElseThrow(() -> new RuntimeException(
						"Student is not assigned to this target"));

		if (targetStudent.getFeeCalculatedAt() == null) {
			throw new RuntimeException(
					"Fee has not been calculated yet for this student-target combination. " +
							"Fees must be frozen before contributions can be collected.");
		}

		// Calculate commissions
		BigDecimal platformCommission = request.getValue()
				.multiply(request.getPlatformCommissionRate())
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		BigDecimal operatorFee = platformCommission
				.multiply(request.getOperatorFeeRate())
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		// Create contribution
		Contribution contribution = new Contribution();
		contribution.setPublicId(UUID.randomUUID());
		contribution.setValue(request.getValue());
		contribution.setPlatformCommissionReserved(platformCommission);
		contribution.setOperatorFee(operatorFee);
		contribution.setOperatorFeeStatus("PENDING");
		contribution.setTarget(target);
		contribution.setStudent(student);
		contribution.setCreatedAt(LocalDateTime.now());

		contributionRepository.save(contribution);

		log.info("Contribution created: value={}, platformCommission={}, operatorFee={}, " +
				"netToTarget={}, platformProfit={}",
				request.getValue(), platformCommission, operatorFee,
				request.getValue().subtract(platformCommission),
				platformCommission.subtract(operatorFee));
	}

	@Transactional(readOnly = true)
	public List<ContributionResponse> getContributionsByTarget(String targetId) {
		UUID targetPublicId = UUID.fromString(targetId);
		Target target = targetRepository.findByPublicId(targetPublicId)
				.orElseThrow(() -> new RuntimeException("Target not found: " + targetId));
		List<Contribution> contributions = contributionRepository.findByTargetOrderByCreatedAtDesc(target);
		return mapToResponses(contributions);
	}

	@Transactional(readOnly = true)
	public List<ContributionResponse> getContributionsByStudent(String studentId) {
		UUID studentPublicId = UUID.fromString(studentId);
		Student student = studentRepository.findByPublicId(studentPublicId)
				.orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
		List<Contribution> contributions = contributionRepository.findByStudentOrderByCreatedAtDesc(student);
		return mapToResponses(contributions);
	}

	private List<ContributionResponse> mapToResponses(List<Contribution> contributions) {
		List<ContributionResponse> responses = new ArrayList<>(contributions.size());
		for (Contribution contribution : contributions) {
			responses.add(mapToResponse(contribution));
		}
		return responses;
	}

	private ContributionResponse mapToResponse(Contribution contribution) {
		return ContributionResponse.builder()
				.publicId(contribution.getPublicId().toString())
				.value(contribution.getValue())
				.platformCommissionReserved(contribution.getPlatformCommissionReserved())
				.operatorFee(contribution.getOperatorFee())
				.operatorFeeStatus(contribution.getOperatorFeeStatus())
				.operatorFeeSettledAt(contribution.getOperatorFeeSettledAt())
				.netToTarget(contribution.getNetToTarget())
				.platformProfit(contribution.getPlatformProfit())
				.studentId(contribution.getStudent().getPublicId().toString())
				.targetId(contribution.getTarget().getPublicId().toString())
				.createdAt(contribution.getCreatedAt())
				.build();
	}
}
