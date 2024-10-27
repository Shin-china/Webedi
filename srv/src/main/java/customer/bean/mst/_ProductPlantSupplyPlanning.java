/**
  * Copyright 2024 bejson.com 
  */
package customer.bean.mst;

import java.math.BigDecimal;
import java.util.List;

public class _ProductPlantSupplyPlanning {

    private String Product;
    private String Plant;
    private BigDecimal FixedLotSizeQuantity;
    private BigDecimal MaximumLotSizeQuantity;
    private BigDecimal MinimumLotSizeQuantity;
    private BigDecimal LotSizeRoundingQuantity;
    private String LotSizingProcedure;
    private String MRPType;
    private String MRPResponsible;
    private BigDecimal SafetyStockQuantity;
    private BigDecimal MinimumSafetyStockQuantity;
    private String PlanningTimeFence;
    private String ConsumptionValueCategory;
    private BigDecimal MaximumStockQuantity;
    private BigDecimal ReorderThresholdQuantity;
    private BigDecimal PlannedDeliveryDurationInDays;
    private String SafetySupplyDurationInDays;
    private String PlanningStrategyGroup;
    private BigDecimal TotalReplenishmentLeadTime;
    private String ProcurementType;
    private String ProcurementSubType;
    private BigDecimal AssemblyScrapPercent;
    private String AvailabilityCheckType;
    private BigDecimal GoodsReceiptDuration;
    private String PlanAndOrderDayDetermination;
    private String RoundingProfile;
    private String DfltStorageLocationExtProcmt;
    private String MRPGroup;
    private BigDecimal LotSizeIndependentCosts;
    private BigDecimal RqmtQtyRcptTaktTmeInWrkgDays;
    private String MRPPlanningCalendar;
    private String RangeOfCvrgPrflCode;
    private String ProductSafetyTimeMRPRelevance;
    private String SafetyTimePeriodProfile;
    private String DependentRqmtMRPRelevance;
    private BigDecimal ProductServiceLevelInPercent;
    private BigDecimal ProdInhProdnDurationInWorkDays;
    private String MRPAvailabilityType;
    private String CrossProjectProduct;
    private String StorageCostsPercentageCode;
    private String FollowUpProduct;
    private boolean RepetitiveManufacturingIsAllwd;
    private String DependentRequirementsType;
    private boolean ProductIsBulkComponent;
    private String RepetitiveManufacturingProfile;
    private String BackwardCnsmpnPeriodInWorkDays;
    private String FwdConsumptionPeriodInWorkDays;
    private String ProdRqmtsConsumptionMode;
    private String ProdFcstRequirementsSplitCode;
    private String EffectiveOutDate;
    private String SchedulingFloatProfile;
    private BigDecimal ComponentScrapInPercent;
    private String ProductDiscontinuationCode;
    private String ProductRequirementsGrouping;
    private String ProductionInvtryManagedLoc;
    private String ProductComponentBackflushCode;
    private String ProposedProductSupplyArea;
    private String MRPSafetyStockMethod;
    private String JITProdnConfProfile;
    private String Currency;
    private String BaseUnit;
    private String ProdnPlngAndControlCalendar;
    private String BaseISOUnit;
    private String ConsignmentControl;
    private BigDecimal ATPCheckHorizonInDays;
    private String ATPCheckHorizonFactoryCalendar;
    private List<String> SAP__Messages;

    public void setProduct(String Product) {
        this.Product = Product;
    }

    public String getProduct() {
        return Product;
    }

    public void setPlant(String Plant) {
        this.Plant = Plant;
    }

    public String getPlant() {
        return Plant;
    }

    /**
     * @return the fixedLotSizeQuantity
     */
    public BigDecimal getFixedLotSizeQuantity() {
        return FixedLotSizeQuantity;
    }

    /**
     * @param fixedLotSizeQuantity the fixedLotSizeQuantity to set
     */
    public void setFixedLotSizeQuantity(BigDecimal fixedLotSizeQuantity) {
        FixedLotSizeQuantity = fixedLotSizeQuantity;
    }

    /**
     * @return the maximumLotSizeQuantity
     */
    public BigDecimal getMaximumLotSizeQuantity() {
        return MaximumLotSizeQuantity;
    }

    /**
     * @param maximumLotSizeQuantity the maximumLotSizeQuantity to set
     */
    public void setMaximumLotSizeQuantity(BigDecimal maximumLotSizeQuantity) {
        MaximumLotSizeQuantity = maximumLotSizeQuantity;
    }

    /**
     * @return the minimumLotSizeQuantity
     */
    public BigDecimal getMinimumLotSizeQuantity() {
        return MinimumLotSizeQuantity;
    }

    /**
     * @param minimumLotSizeQuantity the minimumLotSizeQuantity to set
     */
    public void setMinimumLotSizeQuantity(BigDecimal minimumLotSizeQuantity) {
        MinimumLotSizeQuantity = minimumLotSizeQuantity;
    }

    /**
     * @return the lotSizeRoundingQuantity
     */
    public BigDecimal getLotSizeRoundingQuantity() {
        return LotSizeRoundingQuantity;
    }

    /**
     * @param lotSizeRoundingQuantity the lotSizeRoundingQuantity to set
     */
    public void setLotSizeRoundingQuantity(BigDecimal lotSizeRoundingQuantity) {
        LotSizeRoundingQuantity = lotSizeRoundingQuantity;
    }

    /**
     * @return the lotSizingProcedure
     */
    public String getLotSizingProcedure() {
        return LotSizingProcedure;
    }

    /**
     * @param lotSizingProcedure the lotSizingProcedure to set
     */
    public void setLotSizingProcedure(String lotSizingProcedure) {
        LotSizingProcedure = lotSizingProcedure;
    }

    /**
     * @return the mRPType
     */
    public String getMRPType() {
        return MRPType;
    }

    /**
     * @param mRPType the mRPType to set
     */
    public void setMRPType(String mRPType) {
        MRPType = mRPType;
    }

    /**
     * @return the mRPResponsible
     */
    public String getMRPResponsible() {
        return MRPResponsible;
    }

    /**
     * @param mRPResponsible the mRPResponsible to set
     */
    public void setMRPResponsible(String mRPResponsible) {
        MRPResponsible = mRPResponsible;
    }

    /**
     * @return the safetyStockQuantity
     */
    public BigDecimal getSafetyStockQuantity() {
        return SafetyStockQuantity;
    }

    /**
     * @param safetyStockQuantity the safetyStockQuantity to set
     */
    public void setSafetyStockQuantity(BigDecimal safetyStockQuantity) {
        SafetyStockQuantity = safetyStockQuantity;
    }

    /**
     * @return the minimumSafetyStockQuantity
     */
    public BigDecimal getMinimumSafetyStockQuantity() {
        return MinimumSafetyStockQuantity;
    }

    /**
     * @param minimumSafetyStockQuantity the minimumSafetyStockQuantity to set
     */
    public void setMinimumSafetyStockQuantity(BigDecimal minimumSafetyStockQuantity) {
        MinimumSafetyStockQuantity = minimumSafetyStockQuantity;
    }

    /**
     * @return the planningTimeFence
     */
    public String getPlanningTimeFence() {
        return PlanningTimeFence;
    }

    /**
     * @param planningTimeFence the planningTimeFence to set
     */
    public void setPlanningTimeFence(String planningTimeFence) {
        PlanningTimeFence = planningTimeFence;
    }

    /**
     * @return the consumptionValueCategory
     */
    public String getConsumptionValueCategory() {
        return ConsumptionValueCategory;
    }

    /**
     * @param consumptionValueCategory the consumptionValueCategory to set
     */
    public void setConsumptionValueCategory(String consumptionValueCategory) {
        ConsumptionValueCategory = consumptionValueCategory;
    }

    /**
     * @return the maximumStockQuantity
     */
    public BigDecimal getMaximumStockQuantity() {
        return MaximumStockQuantity;
    }

    /**
     * @param maximumStockQuantity the maximumStockQuantity to set
     */
    public void setMaximumStockQuantity(BigDecimal maximumStockQuantity) {
        MaximumStockQuantity = maximumStockQuantity;
    }

    /**
     * @return the reorderThresholdQuantity
     */
    public BigDecimal getReorderThresholdQuantity() {
        return ReorderThresholdQuantity;
    }

    /**
     * @param reorderThresholdQuantity the reorderThresholdQuantity to set
     */
    public void setReorderThresholdQuantity(BigDecimal reorderThresholdQuantity) {
        ReorderThresholdQuantity = reorderThresholdQuantity;
    }

    /**
     * @return the plannedDeliveryDurationInDays
     */
    public BigDecimal getPlannedDeliveryDurationInDays() {
        return PlannedDeliveryDurationInDays;
    }

    /**
     * @param plannedDeliveryDurationInDays the plannedDeliveryDurationInDays to set
     */
    public void setPlannedDeliveryDurationInDays(BigDecimal plannedDeliveryDurationInDays) {
        PlannedDeliveryDurationInDays = plannedDeliveryDurationInDays;
    }

    /**
     * @return the safetySupplyDurationInDays
     */
    public String getSafetySupplyDurationInDays() {
        return SafetySupplyDurationInDays;
    }

    /**
     * @param safetySupplyDurationInDays the safetySupplyDurationInDays to set
     */
    public void setSafetySupplyDurationInDays(String safetySupplyDurationInDays) {
        SafetySupplyDurationInDays = safetySupplyDurationInDays;
    }

    /**
     * @return the planningStrategyGroup
     */
    public String getPlanningStrategyGroup() {
        return PlanningStrategyGroup;
    }

    /**
     * @param planningStrategyGroup the planningStrategyGroup to set
     */
    public void setPlanningStrategyGroup(String planningStrategyGroup) {
        PlanningStrategyGroup = planningStrategyGroup;
    }

    /**
     * @return the totalReplenishmentLeadTime
     */
    public BigDecimal getTotalReplenishmentLeadTime() {
        return TotalReplenishmentLeadTime;
    }

    /**
     * @param totalReplenishmentLeadTime the totalReplenishmentLeadTime to set
     */
    public void setTotalReplenishmentLeadTime(BigDecimal totalReplenishmentLeadTime) {
        TotalReplenishmentLeadTime = totalReplenishmentLeadTime;
    }

    /**
     * @return the procurementType
     */
    public String getProcurementType() {
        return ProcurementType;
    }

    /**
     * @param procurementType the procurementType to set
     */
    public void setProcurementType(String procurementType) {
        ProcurementType = procurementType;
    }

    /**
     * @return the procurementSubType
     */
    public String getProcurementSubType() {
        return ProcurementSubType;
    }

    /**
     * @param procurementSubType the procurementSubType to set
     */
    public void setProcurementSubType(String procurementSubType) {
        ProcurementSubType = procurementSubType;
    }

    /**
     * @return the assemblyScrapPercent
     */
    public BigDecimal getAssemblyScrapPercent() {
        return AssemblyScrapPercent;
    }

    /**
     * @param assemblyScrapPercent the assemblyScrapPercent to set
     */
    public void setAssemblyScrapPercent(BigDecimal assemblyScrapPercent) {
        AssemblyScrapPercent = assemblyScrapPercent;
    }

    /**
     * @return the availabilityCheckType
     */
    public String getAvailabilityCheckType() {
        return AvailabilityCheckType;
    }

    /**
     * @param availabilityCheckType the availabilityCheckType to set
     */
    public void setAvailabilityCheckType(String availabilityCheckType) {
        AvailabilityCheckType = availabilityCheckType;
    }

    /**
     * @return the goodsReceiptDuration
     */
    public BigDecimal getGoodsReceiptDuration() {
        return GoodsReceiptDuration;
    }

    /**
     * @param goodsReceiptDuration the goodsReceiptDuration to set
     */
    public void setGoodsReceiptDuration(BigDecimal goodsReceiptDuration) {
        GoodsReceiptDuration = goodsReceiptDuration;
    }

    /**
     * @return the planAndOrderDayDetermination
     */
    public String getPlanAndOrderDayDetermination() {
        return PlanAndOrderDayDetermination;
    }

    /**
     * @param planAndOrderDayDetermination the planAndOrderDayDetermination to set
     */
    public void setPlanAndOrderDayDetermination(String planAndOrderDayDetermination) {
        PlanAndOrderDayDetermination = planAndOrderDayDetermination;
    }

    /**
     * @return the roundingProfile
     */
    public String getRoundingProfile() {
        return RoundingProfile;
    }

    /**
     * @param roundingProfile the roundingProfile to set
     */
    public void setRoundingProfile(String roundingProfile) {
        RoundingProfile = roundingProfile;
    }

    /**
     * @return the dfltStorageLocationExtProcmt
     */
    public String getDfltStorageLocationExtProcmt() {
        return DfltStorageLocationExtProcmt;
    }

    /**
     * @param dfltStorageLocationExtProcmt the dfltStorageLocationExtProcmt to set
     */
    public void setDfltStorageLocationExtProcmt(String dfltStorageLocationExtProcmt) {
        DfltStorageLocationExtProcmt = dfltStorageLocationExtProcmt;
    }

    /**
     * @return the mRPGroup
     */
    public String getMRPGroup() {
        return MRPGroup;
    }

    /**
     * @param mRPGroup the mRPGroup to set
     */
    public void setMRPGroup(String mRPGroup) {
        MRPGroup = mRPGroup;
    }

    /**
     * @return the lotSizeIndependentCosts
     */
    public BigDecimal getLotSizeIndependentCosts() {
        return LotSizeIndependentCosts;
    }

    /**
     * @param lotSizeIndependentCosts the lotSizeIndependentCosts to set
     */
    public void setLotSizeIndependentCosts(BigDecimal lotSizeIndependentCosts) {
        LotSizeIndependentCosts = lotSizeIndependentCosts;
    }

    /**
     * @return the rqmtQtyRcptTaktTmeInWrkgDays
     */
    public BigDecimal getRqmtQtyRcptTaktTmeInWrkgDays() {
        return RqmtQtyRcptTaktTmeInWrkgDays;
    }

    /**
     * @param rqmtQtyRcptTaktTmeInWrkgDays the rqmtQtyRcptTaktTmeInWrkgDays to set
     */
    public void setRqmtQtyRcptTaktTmeInWrkgDays(BigDecimal rqmtQtyRcptTaktTmeInWrkgDays) {
        RqmtQtyRcptTaktTmeInWrkgDays = rqmtQtyRcptTaktTmeInWrkgDays;
    }

    /**
     * @return the mRPPlanningCalendar
     */
    public String getMRPPlanningCalendar() {
        return MRPPlanningCalendar;
    }

    /**
     * @param mRPPlanningCalendar the mRPPlanningCalendar to set
     */
    public void setMRPPlanningCalendar(String mRPPlanningCalendar) {
        MRPPlanningCalendar = mRPPlanningCalendar;
    }

    /**
     * @return the rangeOfCvrgPrflCode
     */
    public String getRangeOfCvrgPrflCode() {
        return RangeOfCvrgPrflCode;
    }

    /**
     * @param rangeOfCvrgPrflCode the rangeOfCvrgPrflCode to set
     */
    public void setRangeOfCvrgPrflCode(String rangeOfCvrgPrflCode) {
        RangeOfCvrgPrflCode = rangeOfCvrgPrflCode;
    }

    /**
     * @return the productSafetyTimeMRPRelevance
     */
    public String getProductSafetyTimeMRPRelevance() {
        return ProductSafetyTimeMRPRelevance;
    }

    /**
     * @param productSafetyTimeMRPRelevance the productSafetyTimeMRPRelevance to set
     */
    public void setProductSafetyTimeMRPRelevance(String productSafetyTimeMRPRelevance) {
        ProductSafetyTimeMRPRelevance = productSafetyTimeMRPRelevance;
    }

    /**
     * @return the safetyTimePeriodProfile
     */
    public String getSafetyTimePeriodProfile() {
        return SafetyTimePeriodProfile;
    }

    /**
     * @param safetyTimePeriodProfile the safetyTimePeriodProfile to set
     */
    public void setSafetyTimePeriodProfile(String safetyTimePeriodProfile) {
        SafetyTimePeriodProfile = safetyTimePeriodProfile;
    }

    /**
     * @return the dependentRqmtMRPRelevance
     */
    public String getDependentRqmtMRPRelevance() {
        return DependentRqmtMRPRelevance;
    }

    /**
     * @param dependentRqmtMRPRelevance the dependentRqmtMRPRelevance to set
     */
    public void setDependentRqmtMRPRelevance(String dependentRqmtMRPRelevance) {
        DependentRqmtMRPRelevance = dependentRqmtMRPRelevance;
    }

    /**
     * @return the productServiceLevelInPercent
     */
    public BigDecimal getProductServiceLevelInPercent() {
        return ProductServiceLevelInPercent;
    }

    /**
     * @param productServiceLevelInPercent the productServiceLevelInPercent to set
     */
    public void setProductServiceLevelInPercent(BigDecimal productServiceLevelInPercent) {
        ProductServiceLevelInPercent = productServiceLevelInPercent;
    }

    /**
     * @return the prodInhProdnDurationInWorkDays
     */
    public BigDecimal getProdInhProdnDurationInWorkDays() {
        return ProdInhProdnDurationInWorkDays;
    }

    /**
     * @param prodInhProdnDurationInWorkDays the prodInhProdnDurationInWorkDays to
     *                                       set
     */
    public void setProdInhProdnDurationInWorkDays(BigDecimal prodInhProdnDurationInWorkDays) {
        ProdInhProdnDurationInWorkDays = prodInhProdnDurationInWorkDays;
    }

    /**
     * @return the mRPAvailabilityType
     */
    public String getMRPAvailabilityType() {
        return MRPAvailabilityType;
    }

    /**
     * @param mRPAvailabilityType the mRPAvailabilityType to set
     */
    public void setMRPAvailabilityType(String mRPAvailabilityType) {
        MRPAvailabilityType = mRPAvailabilityType;
    }

    /**
     * @return the crossProjectProduct
     */
    public String getCrossProjectProduct() {
        return CrossProjectProduct;
    }

    /**
     * @param crossProjectProduct the crossProjectProduct to set
     */
    public void setCrossProjectProduct(String crossProjectProduct) {
        CrossProjectProduct = crossProjectProduct;
    }

    /**
     * @return the storageCostsPercentageCode
     */
    public String getStorageCostsPercentageCode() {
        return StorageCostsPercentageCode;
    }

    /**
     * @param storageCostsPercentageCode the storageCostsPercentageCode to set
     */
    public void setStorageCostsPercentageCode(String storageCostsPercentageCode) {
        StorageCostsPercentageCode = storageCostsPercentageCode;
    }

    /**
     * @return the followUpProduct
     */
    public String getFollowUpProduct() {
        return FollowUpProduct;
    }

    /**
     * @param followUpProduct the followUpProduct to set
     */
    public void setFollowUpProduct(String followUpProduct) {
        FollowUpProduct = followUpProduct;
    }

    /**
     * @return the repetitiveManufacturingIsAllwd
     */
    public boolean isRepetitiveManufacturingIsAllwd() {
        return RepetitiveManufacturingIsAllwd;
    }

    /**
     * @param repetitiveManufacturingIsAllwd the repetitiveManufacturingIsAllwd to
     *                                       set
     */
    public void setRepetitiveManufacturingIsAllwd(boolean repetitiveManufacturingIsAllwd) {
        RepetitiveManufacturingIsAllwd = repetitiveManufacturingIsAllwd;
    }

    /**
     * @return the dependentRequirementsType
     */
    public String getDependentRequirementsType() {
        return DependentRequirementsType;
    }

    /**
     * @param dependentRequirementsType the dependentRequirementsType to set
     */
    public void setDependentRequirementsType(String dependentRequirementsType) {
        DependentRequirementsType = dependentRequirementsType;
    }

    /**
     * @return the productIsBulkComponent
     */
    public boolean isProductIsBulkComponent() {
        return ProductIsBulkComponent;
    }

    /**
     * @param productIsBulkComponent the productIsBulkComponent to set
     */
    public void setProductIsBulkComponent(boolean productIsBulkComponent) {
        ProductIsBulkComponent = productIsBulkComponent;
    }

    /**
     * @return the repetitiveManufacturingProfile
     */
    public String getRepetitiveManufacturingProfile() {
        return RepetitiveManufacturingProfile;
    }

    /**
     * @param repetitiveManufacturingProfile the repetitiveManufacturingProfile to
     *                                       set
     */
    public void setRepetitiveManufacturingProfile(String repetitiveManufacturingProfile) {
        RepetitiveManufacturingProfile = repetitiveManufacturingProfile;
    }

    /**
     * @return the backwardCnsmpnPeriodInWorkDays
     */
    public String getBackwardCnsmpnPeriodInWorkDays() {
        return BackwardCnsmpnPeriodInWorkDays;
    }

    /**
     * @param backwardCnsmpnPeriodInWorkDays the backwardCnsmpnPeriodInWorkDays to
     *                                       set
     */
    public void setBackwardCnsmpnPeriodInWorkDays(String backwardCnsmpnPeriodInWorkDays) {
        BackwardCnsmpnPeriodInWorkDays = backwardCnsmpnPeriodInWorkDays;
    }

    /**
     * @return the fwdConsumptionPeriodInWorkDays
     */
    public String getFwdConsumptionPeriodInWorkDays() {
        return FwdConsumptionPeriodInWorkDays;
    }

    /**
     * @param fwdConsumptionPeriodInWorkDays the fwdConsumptionPeriodInWorkDays to
     *                                       set
     */
    public void setFwdConsumptionPeriodInWorkDays(String fwdConsumptionPeriodInWorkDays) {
        FwdConsumptionPeriodInWorkDays = fwdConsumptionPeriodInWorkDays;
    }

    /**
     * @return the prodRqmtsConsumptionMode
     */
    public String getProdRqmtsConsumptionMode() {
        return ProdRqmtsConsumptionMode;
    }

    /**
     * @param prodRqmtsConsumptionMode the prodRqmtsConsumptionMode to set
     */
    public void setProdRqmtsConsumptionMode(String prodRqmtsConsumptionMode) {
        ProdRqmtsConsumptionMode = prodRqmtsConsumptionMode;
    }

    /**
     * @return the prodFcstRequirementsSplitCode
     */
    public String getProdFcstRequirementsSplitCode() {
        return ProdFcstRequirementsSplitCode;
    }

    /**
     * @param prodFcstRequirementsSplitCode the prodFcstRequirementsSplitCode to set
     */
    public void setProdFcstRequirementsSplitCode(String prodFcstRequirementsSplitCode) {
        ProdFcstRequirementsSplitCode = prodFcstRequirementsSplitCode;
    }

    /**
     * @return the effectiveOutDate
     */
    public String getEffectiveOutDate() {
        return EffectiveOutDate;
    }

    /**
     * @param effectiveOutDate the effectiveOutDate to set
     */
    public void setEffectiveOutDate(String effectiveOutDate) {
        EffectiveOutDate = effectiveOutDate;
    }

    /**
     * @return the schedulingFloatProfile
     */
    public String getSchedulingFloatProfile() {
        return SchedulingFloatProfile;
    }

    /**
     * @param schedulingFloatProfile the schedulingFloatProfile to set
     */
    public void setSchedulingFloatProfile(String schedulingFloatProfile) {
        SchedulingFloatProfile = schedulingFloatProfile;
    }

    /**
     * @return the componentScrapInPercent
     */
    public BigDecimal getComponentScrapInPercent() {
        return ComponentScrapInPercent;
    }

    /**
     * @param componentScrapInPercent the componentScrapInPercent to set
     */
    public void setComponentScrapInPercent(BigDecimal componentScrapInPercent) {
        ComponentScrapInPercent = componentScrapInPercent;
    }

    /**
     * @return the productDiscontinuationCode
     */
    public String getProductDiscontinuationCode() {
        return ProductDiscontinuationCode;
    }

    /**
     * @param productDiscontinuationCode the productDiscontinuationCode to set
     */
    public void setProductDiscontinuationCode(String productDiscontinuationCode) {
        ProductDiscontinuationCode = productDiscontinuationCode;
    }

    /**
     * @return the productRequirementsGrouping
     */
    public String getProductRequirementsGrouping() {
        return ProductRequirementsGrouping;
    }

    /**
     * @param productRequirementsGrouping the productRequirementsGrouping to set
     */
    public void setProductRequirementsGrouping(String productRequirementsGrouping) {
        ProductRequirementsGrouping = productRequirementsGrouping;
    }

    /**
     * @return the productionInvtryManagedLoc
     */
    public String getProductionInvtryManagedLoc() {
        return ProductionInvtryManagedLoc;
    }

    /**
     * @param productionInvtryManagedLoc the productionInvtryManagedLoc to set
     */
    public void setProductionInvtryManagedLoc(String productionInvtryManagedLoc) {
        ProductionInvtryManagedLoc = productionInvtryManagedLoc;
    }

    /**
     * @return the productComponentBackflushCode
     */
    public String getProductComponentBackflushCode() {
        return ProductComponentBackflushCode;
    }

    /**
     * @param productComponentBackflushCode the productComponentBackflushCode to set
     */
    public void setProductComponentBackflushCode(String productComponentBackflushCode) {
        ProductComponentBackflushCode = productComponentBackflushCode;
    }

    /**
     * @return the proposedProductSupplyArea
     */
    public String getProposedProductSupplyArea() {
        return ProposedProductSupplyArea;
    }

    /**
     * @param proposedProductSupplyArea the proposedProductSupplyArea to set
     */
    public void setProposedProductSupplyArea(String proposedProductSupplyArea) {
        ProposedProductSupplyArea = proposedProductSupplyArea;
    }

    /**
     * @return the mRPSafetyStockMethod
     */
    public String getMRPSafetyStockMethod() {
        return MRPSafetyStockMethod;
    }

    /**
     * @param mRPSafetyStockMethod the mRPSafetyStockMethod to set
     */
    public void setMRPSafetyStockMethod(String mRPSafetyStockMethod) {
        MRPSafetyStockMethod = mRPSafetyStockMethod;
    }

    /**
     * @return the jITProdnConfProfile
     */
    public String getJITProdnConfProfile() {
        return JITProdnConfProfile;
    }

    /**
     * @param jITProdnConfProfile the jITProdnConfProfile to set
     */
    public void setJITProdnConfProfile(String jITProdnConfProfile) {
        JITProdnConfProfile = jITProdnConfProfile;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return Currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        Currency = currency;
    }

    /**
     * @return the baseUnit
     */
    public String getBaseUnit() {
        return BaseUnit;
    }

    /**
     * @param baseUnit the baseUnit to set
     */
    public void setBaseUnit(String baseUnit) {
        BaseUnit = baseUnit;
    }

    /**
     * @return the prodnPlngAndControlCalendar
     */
    public String getProdnPlngAndControlCalendar() {
        return ProdnPlngAndControlCalendar;
    }

    /**
     * @param prodnPlngAndControlCalendar the prodnPlngAndControlCalendar to set
     */
    public void setProdnPlngAndControlCalendar(String prodnPlngAndControlCalendar) {
        ProdnPlngAndControlCalendar = prodnPlngAndControlCalendar;
    }

    /**
     * @return the baseISOUnit
     */
    public String getBaseISOUnit() {
        return BaseISOUnit;
    }

    /**
     * @param baseISOUnit the baseISOUnit to set
     */
    public void setBaseISOUnit(String baseISOUnit) {
        BaseISOUnit = baseISOUnit;
    }

    /**
     * @return the consignmentControl
     */
    public String getConsignmentControl() {
        return ConsignmentControl;
    }

    /**
     * @param consignmentControl the consignmentControl to set
     */
    public void setConsignmentControl(String consignmentControl) {
        ConsignmentControl = consignmentControl;
    }

    /**
     * @return the aTPCheckHorizonInDays
     */
    public BigDecimal getATPCheckHorizonInDays() {
        return ATPCheckHorizonInDays;
    }

    /**
     * @param aTPCheckHorizonInDays the aTPCheckHorizonInDays to set
     */
    public void setATPCheckHorizonInDays(BigDecimal aTPCheckHorizonInDays) {
        ATPCheckHorizonInDays = aTPCheckHorizonInDays;
    }

    /**
     * @return the aTPCheckHorizonFactoryCalendar
     */
    public String getATPCheckHorizonFactoryCalendar() {
        return ATPCheckHorizonFactoryCalendar;
    }

    /**
     * @param aTPCheckHorizonFactoryCalendar the aTPCheckHorizonFactoryCalendar to
     *                                       set
     */
    public void setATPCheckHorizonFactoryCalendar(String aTPCheckHorizonFactoryCalendar) {
        ATPCheckHorizonFactoryCalendar = aTPCheckHorizonFactoryCalendar;
    }

    /**
     * @return the sAP__Messages
     */
    public List<String> getSAP__Messages() {
        return SAP__Messages;
    }

    /**
     * @param sAP__Messages the sAP__Messages to set
     */
    public void setSAP__Messages(List<String> sAP__Messages) {
        SAP__Messages = sAP__Messages;
    }

}