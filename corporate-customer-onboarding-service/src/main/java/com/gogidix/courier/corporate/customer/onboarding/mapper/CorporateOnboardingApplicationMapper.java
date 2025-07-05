package com.gogidix.courier.corporate.customer.onboarding.mapper;

import com.gogidix.courier.corporate.customer.onboarding.dto.CorporateOnboardingApplicationResponse;
import com.gogidix.courier.corporate.customer.onboarding.dto.CreateCorporateOnboardingApplicationRequest;
import com.gogidix.courier.corporate.customer.onboarding.model.CorporateOnboardingApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for CorporateOnboardingApplication entity and DTOs.
 * 
 * Provides automatic mapping between entities and data transfer objects
 * with comprehensive field mapping and business logic integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CorporateOnboardingApplicationMapper {

    /**
     * Map entity to response DTO.
     */
    @Mapping(target = "hasBillingContact", expression = "java(application.getBillingContactFirstName() != null && application.getBillingContactLastName() != null && application.getBillingContactEmail() != null)")
    CorporateOnboardingApplicationResponse toResponse(CorporateOnboardingApplication application);

    /**
     * Map creation request to entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "applicationStatus", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectedAt", ignore = true)
    @Mapping(target = "rejectedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "kybVerificationId", ignore = true)
    @Mapping(target = "authServiceUserId", ignore = true)
    @Mapping(target = "billingCustomerId", ignore = true)
    @Mapping(target = "approvedCreditLimit", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "verificationDocuments", ignore = true)
    @Mapping(source = "preferredPaymentTerms", target = "paymentTerms")
    @Mapping(target = "volumeDiscountTier", expression = "java(request.getRecommendedDiscountTier())")
    CorporateOnboardingApplication toEntity(CreateCorporateOnboardingApplicationRequest request);

    /**
     * Update existing entity from request (for partial updates).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "applicationReferenceId", ignore = true)
    @Mapping(target = "companyRegistrationNumber", ignore = true)
    @Mapping(target = "applicationStatus", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectedAt", ignore = true)
    @Mapping(target = "rejectedBy", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "kybVerificationId", ignore = true)
    @Mapping(target = "authServiceUserId", ignore = true)
    @Mapping(target = "billingCustomerId", ignore = true)
    @Mapping(target = "approvedCreditLimit", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "verificationDocuments", ignore = true)
    @Mapping(target = "termsAccepted", ignore = true)
    @Mapping(target = "privacyPolicyAccepted", ignore = true)
    @Mapping(target = "dataProcessingAgreementAccepted", ignore = true)
    @Mapping(target = "volumeDiscountTier", ignore = true)
    void updateEntityFromRequest(com.gogidix.courier.corporate.customer.onboarding.dto.UpdateCorporateOnboardingApplicationRequest request, 
                                @MappingTarget CorporateOnboardingApplication application);

    /**
     * Create a simple response for list views (lighter version).
     */
    @Mapping(target = "slaRequirements", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    CorporateOnboardingApplicationResponse toListResponse(CorporateOnboardingApplication application);
}