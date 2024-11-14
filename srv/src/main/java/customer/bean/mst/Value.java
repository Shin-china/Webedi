package customer.bean.mst;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Data
public class Value {

    private String Product;
    private String ProductType;
    private String CreationDate;
    private String CreationTime;
    private String CreationDateTime;
    private String CreatedByUser;
    private String LastChangeDate;
    private String LastChangedByUser;
    private boolean IsMarkedForDeletion;
    private String CrossPlantStatus;
    private String CrossPlantStatusValidityDate;
    private String ProductOldID;
    private BigDecimal GrossWeight;
    private String WeightUnit;
    private String WeightISOUnit;
    private String ProductGroup;
    private String BaseUnit;
    private String BaseISOUnit;
    private String ItemCategoryGroup;
    private BigDecimal NetWeight;
    private String Division;
    private String VolumeUnit;
    private String VolumeISOUnit;
    private BigDecimal ProductVolume;
    private String AuthorizationGroup;
    private String ANPCode;
    private String SizeOrDimensionText;
    private String IndustryStandardName;
    private String ProductStandardID;
    private String InternationalArticleNumberCat;
    private boolean ProductIsConfigurable;
    private boolean IsBatchManagementRequired;
    private String ExternalProductGroup;
    private String CrossPlantConfigurableProduct;
    private String SerialNoExplicitnessLevel;
    private boolean IsApprovedBatchRecordReqd;
    private String HandlingIndicator;
    private String WarehouseProductGroup;
    private String WarehouseStorageCondition;
    private String StandardHandlingUnitType;
    private String SerialNumberProfile;
    private boolean IsPilferable;
    private boolean IsRelevantForHzdsSubstances;
    private BigDecimal QuarantinePeriod;
    private String TimeUnitForQuarantinePeriod;
    private String QuarantinePeriodISOUnit;
    private String QualityInspectionGroup;
    private String HandlingUnitType;
    private boolean HasVariableTareWeight;
    private BigDecimal MaximumPackagingLength;
    private BigDecimal MaximumPackagingWidth;
    private BigDecimal MaximumPackagingHeight;
    private BigDecimal MaximumCapacity;
    private BigDecimal OvercapacityTolerance;
    private String UnitForMaxPackagingDimensions;
    private String MaxPackggDimensionISOUnit;
    private BigDecimal BaseUnitSpecificProductLength;
    private BigDecimal BaseUnitSpecificProductWidth;
    private BigDecimal BaseUnitSpecificProductHeight;
    private String ProductMeasurementUnit;
    private String ProductMeasurementISOUnit;
    private String ArticleCategory;
    private String IndustrySector;
    private String LastChangeDateTime;
    private String LastChangeTime;
    private String DangerousGoodsIndProfile;
    private String ProductDocumentChangeNumber;
    private String ProductDocumentPageCount;
    private String ProductDocumentPageNumber;
    private boolean DocumentIsCreatedByCAD;
    private String ProductionOrInspectionMemoTxt;
    private String ProductionMemoPageFormat;
    private boolean ProductIsHighlyViscous;
    private boolean TransportIsInBulk;
    private boolean ProdEffctyParamValsAreAssigned;
    private boolean ProdIsEnvironmentallyRelevant;
    private String LaboratoryOrDesignOffice;
    private String PackagingProductGroup;
    private String PackingReferenceProduct;
    private String BasicProduct;
    private String ProductDocumentNumber;
    private String ProductDocumentVersion;
    private String ProductDocumentType;
    private String ProductDocumentPageFormat;
    private String ProdChmlCmplncRelevanceCode;
    private String DiscountInKindEligibility;
    private String ProductManufacturerNumber;
    private String ManufacturerNumber;
    private String ManufacturerPartProfile;
    private String OwnInventoryManagedProduct;
    private String YY1_CUSTOMERMATERIAL_PRD;
    private List<String> SAP__Messages;
    private List<_ProductDescription> _ProductDescription = new ArrayList<_ProductDescription>();
    private List<_ProductPlant> _ProductPlant = new ArrayList<_ProductPlant>();

    public void setProduct(String Product) {
        this.Product = Product;
    }

    public String getProduct() {
        return Product;
    }

    public void setProductType(String ProductType) {
        this.ProductType = ProductType;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setCreationTime(String CreationTime) {
        this.CreationTime = CreationTime;
    }

    public String getCreationTime() {
        return CreationTime;
    }

    public void setCreatedByUser(String CreatedByUser) {
        this.CreatedByUser = CreatedByUser;
    }

    public String getCreatedByUser() {
        return CreatedByUser;
    }

    public void setLastChangedByUser(String LastChangedByUser) {
        this.LastChangedByUser = LastChangedByUser;
    }

    public String getLastChangedByUser() {
        return LastChangedByUser;
    }

    public void setIsMarkedForDeletion(boolean IsMarkedForDeletion) {
        this.IsMarkedForDeletion = IsMarkedForDeletion;
    }

    public boolean getIsMarkedForDeletion() {
        return IsMarkedForDeletion;
    }

    public void setCrossPlantStatus(String CrossPlantStatus) {
        this.CrossPlantStatus = CrossPlantStatus;
    }

    public String getCrossPlantStatus() {
        return CrossPlantStatus;
    }

    public void setCrossPlantStatusValidityDate(String CrossPlantStatusValidityDate) {
        this.CrossPlantStatusValidityDate = CrossPlantStatusValidityDate;
    }

    public String getCrossPlantStatusValidityDate() {
        return CrossPlantStatusValidityDate;
    }

    public void setProductOldID(String ProductOldID) {
        this.ProductOldID = ProductOldID;
    }

    public String getProductOldID() {
        return ProductOldID;
    }

    public void setWeightUnit(String WeightUnit) {
        this.WeightUnit = WeightUnit;
    }

    public String getWeightUnit() {
        return WeightUnit;
    }

    public void setWeightISOUnit(String WeightISOUnit) {
        this.WeightISOUnit = WeightISOUnit;
    }

    public String getWeightISOUnit() {
        return WeightISOUnit;
    }

    public void setProductGroup(String ProductGroup) {
        this.ProductGroup = ProductGroup;
    }

    public String getProductGroup() {
        return ProductGroup;
    }

    public void setBaseUnit(String BaseUnit) {
        this.BaseUnit = BaseUnit;
    }

    public String getBaseUnit() {
        return BaseUnit;
    }

    public void setBaseISOUnit(String BaseISOUnit) {
        this.BaseISOUnit = BaseISOUnit;
    }

    public String getBaseISOUnit() {
        return BaseISOUnit;
    }

    public void setItemCategoryGroup(String ItemCategoryGroup) {
        this.ItemCategoryGroup = ItemCategoryGroup;
    }

    public String getItemCategoryGroup() {
        return ItemCategoryGroup;
    }

    public void setDivision(String Division) {
        this.Division = Division;
    }

    public String getDivision() {
        return Division;
    }

    public void setVolumeUnit(String VolumeUnit) {
        this.VolumeUnit = VolumeUnit;
    }

    public String getVolumeUnit() {
        return VolumeUnit;
    }

    public void setVolumeISOUnit(String VolumeISOUnit) {
        this.VolumeISOUnit = VolumeISOUnit;
    }

    public String getVolumeISOUnit() {
        return VolumeISOUnit;
    }

    public void setAuthorizationGroup(String AuthorizationGroup) {
        this.AuthorizationGroup = AuthorizationGroup;
    }

    public String getAuthorizationGroup() {
        return AuthorizationGroup;
    }

    public void setANPCode(String ANPCode) {
        this.ANPCode = ANPCode;
    }

    public String getANPCode() {
        return ANPCode;
    }

    public void setSizeOrDimensionText(String SizeOrDimensionText) {
        this.SizeOrDimensionText = SizeOrDimensionText;
    }

    public String getSizeOrDimensionText() {
        return SizeOrDimensionText;
    }

    public void setIndustryStandardName(String IndustryStandardName) {
        this.IndustryStandardName = IndustryStandardName;
    }

    public String getIndustryStandardName() {
        return IndustryStandardName;
    }

    public void setProductStandardID(String ProductStandardID) {
        this.ProductStandardID = ProductStandardID;
    }

    public String getProductStandardID() {
        return ProductStandardID;
    }

    public void setInternationalArticleNumberCat(String InternationalArticleNumberCat) {
        this.InternationalArticleNumberCat = InternationalArticleNumberCat;
    }

    public String getInternationalArticleNumberCat() {
        return InternationalArticleNumberCat;
    }

    public void setProductIsConfigurable(boolean ProductIsConfigurable) {
        this.ProductIsConfigurable = ProductIsConfigurable;
    }

    public boolean getProductIsConfigurable() {
        return ProductIsConfigurable;
    }

    public void setIsBatchManagementRequired(boolean IsBatchManagementRequired) {
        this.IsBatchManagementRequired = IsBatchManagementRequired;
    }

    public boolean getIsBatchManagementRequired() {
        return IsBatchManagementRequired;
    }

    public void setExternalProductGroup(String ExternalProductGroup) {
        this.ExternalProductGroup = ExternalProductGroup;
    }

    public String getExternalProductGroup() {
        return ExternalProductGroup;
    }

    public void setCrossPlantConfigurableProduct(String CrossPlantConfigurableProduct) {
        this.CrossPlantConfigurableProduct = CrossPlantConfigurableProduct;
    }

    public String getCrossPlantConfigurableProduct() {
        return CrossPlantConfigurableProduct;
    }

    public void setSerialNoExplicitnessLevel(String SerialNoExplicitnessLevel) {
        this.SerialNoExplicitnessLevel = SerialNoExplicitnessLevel;
    }

    public String getSerialNoExplicitnessLevel() {
        return SerialNoExplicitnessLevel;
    }

    public void setIsApprovedBatchRecordReqd(boolean IsApprovedBatchRecordReqd) {
        this.IsApprovedBatchRecordReqd = IsApprovedBatchRecordReqd;
    }

    public boolean getIsApprovedBatchRecordReqd() {
        return IsApprovedBatchRecordReqd;
    }

    public void setHandlingIndicator(String HandlingIndicator) {
        this.HandlingIndicator = HandlingIndicator;
    }

    public String getHandlingIndicator() {
        return HandlingIndicator;
    }

    public void setWarehouseProductGroup(String WarehouseProductGroup) {
        this.WarehouseProductGroup = WarehouseProductGroup;
    }

    public String getWarehouseProductGroup() {
        return WarehouseProductGroup;
    }

    public void setWarehouseStorageCondition(String WarehouseStorageCondition) {
        this.WarehouseStorageCondition = WarehouseStorageCondition;
    }

    public String getWarehouseStorageCondition() {
        return WarehouseStorageCondition;
    }

    public void setStandardHandlingUnitType(String StandardHandlingUnitType) {
        this.StandardHandlingUnitType = StandardHandlingUnitType;
    }

    public String getStandardHandlingUnitType() {
        return StandardHandlingUnitType;
    }

    public void setSerialNumberProfile(String SerialNumberProfile) {
        this.SerialNumberProfile = SerialNumberProfile;
    }

    public String getSerialNumberProfile() {
        return SerialNumberProfile;
    }

    public void setIsPilferable(boolean IsPilferable) {
        this.IsPilferable = IsPilferable;
    }

    public boolean getIsPilferable() {
        return IsPilferable;
    }

    public void setIsRelevantForHzdsSubstances(boolean IsRelevantForHzdsSubstances) {
        this.IsRelevantForHzdsSubstances = IsRelevantForHzdsSubstances;
    }

    public boolean getIsRelevantForHzdsSubstances() {
        return IsRelevantForHzdsSubstances;
    }

    public void setTimeUnitForQuarantinePeriod(String TimeUnitForQuarantinePeriod) {
        this.TimeUnitForQuarantinePeriod = TimeUnitForQuarantinePeriod;
    }

    public String getTimeUnitForQuarantinePeriod() {
        return TimeUnitForQuarantinePeriod;
    }

    public void setQuarantinePeriodISOUnit(String QuarantinePeriodISOUnit) {
        this.QuarantinePeriodISOUnit = QuarantinePeriodISOUnit;
    }

    public String getQuarantinePeriodISOUnit() {
        return QuarantinePeriodISOUnit;
    }

    public void setQualityInspectionGroup(String QualityInspectionGroup) {
        this.QualityInspectionGroup = QualityInspectionGroup;
    }

    public String getQualityInspectionGroup() {
        return QualityInspectionGroup;
    }

    public void setHandlingUnitType(String HandlingUnitType) {
        this.HandlingUnitType = HandlingUnitType;
    }

    public String getHandlingUnitType() {
        return HandlingUnitType;
    }

    public void setHasVariableTareWeight(boolean HasVariableTareWeight) {
        this.HasVariableTareWeight = HasVariableTareWeight;
    }

    public boolean getHasVariableTareWeight() {
        return HasVariableTareWeight;
    }

    public void setUnitForMaxPackagingDimensions(String UnitForMaxPackagingDimensions) {
        this.UnitForMaxPackagingDimensions = UnitForMaxPackagingDimensions;
    }

    public String getUnitForMaxPackagingDimensions() {
        return UnitForMaxPackagingDimensions;
    }

    public void setMaxPackggDimensionISOUnit(String MaxPackggDimensionISOUnit) {
        this.MaxPackggDimensionISOUnit = MaxPackggDimensionISOUnit;
    }

    public String getMaxPackggDimensionISOUnit() {
        return MaxPackggDimensionISOUnit;
    }

    /**
     * @return the grossWeight
     */
    public BigDecimal getGrossWeight() {
        return GrossWeight;
    }

    /**
     * @param grossWeight the grossWeight to set
     */
    public void setGrossWeight(BigDecimal grossWeight) {
        GrossWeight = grossWeight;
    }

    /**
     * @return the netWeight
     */
    public BigDecimal getNetWeight() {
        return NetWeight;
    }

    /**
     * @param netWeight the netWeight to set
     */
    public void setNetWeight(BigDecimal netWeight) {
        NetWeight = netWeight;
    }

    /**
     * @return the productVolume
     */
    public BigDecimal getProductVolume() {
        return ProductVolume;
    }

    /**
     * @param productVolume the productVolume to set
     */
    public void setProductVolume(BigDecimal productVolume) {
        ProductVolume = productVolume;
    }

    /**
     * @return the quarantinePeriod
     */
    public BigDecimal getQuarantinePeriod() {
        return QuarantinePeriod;
    }

    /**
     * @param quarantinePeriod the quarantinePeriod to set
     */
    public void setQuarantinePeriod(BigDecimal quarantinePeriod) {
        QuarantinePeriod = quarantinePeriod;
    }

    /**
     * @return the maximumPackagingLength
     */
    public BigDecimal getMaximumPackagingLength() {
        return MaximumPackagingLength;
    }

    /**
     * @param maximumPackagingLength the maximumPackagingLength to set
     */
    public void setMaximumPackagingLength(BigDecimal maximumPackagingLength) {
        MaximumPackagingLength = maximumPackagingLength;
    }

    /**
     * @return the maximumPackagingWidth
     */
    public BigDecimal getMaximumPackagingWidth() {
        return MaximumPackagingWidth;
    }

    /**
     * @param maximumPackagingWidth the maximumPackagingWidth to set
     */
    public void setMaximumPackagingWidth(BigDecimal maximumPackagingWidth) {
        MaximumPackagingWidth = maximumPackagingWidth;
    }

    /**
     * @return the maximumPackagingHeight
     */
    public BigDecimal getMaximumPackagingHeight() {
        return MaximumPackagingHeight;
    }

    /**
     * @param maximumPackagingHeight the maximumPackagingHeight to set
     */
    public void setMaximumPackagingHeight(BigDecimal maximumPackagingHeight) {
        MaximumPackagingHeight = maximumPackagingHeight;
    }

    /**
     * @return the maximumCapacity
     */
    public BigDecimal getMaximumCapacity() {
        return MaximumCapacity;
    }

    /**
     * @param maximumCapacity the maximumCapacity to set
     */
    public void setMaximumCapacity(BigDecimal maximumCapacity) {
        MaximumCapacity = maximumCapacity;
    }

    /**
     * @return the overcapacityTolerance
     */
    public BigDecimal getOvercapacityTolerance() {
        return OvercapacityTolerance;
    }

    /**
     * @param overcapacityTolerance the overcapacityTolerance to set
     */
    public void setOvercapacityTolerance(BigDecimal overcapacityTolerance) {
        OvercapacityTolerance = overcapacityTolerance;
    }

    /**
     * @return the baseUnitSpecificProductLength
     */
    public BigDecimal getBaseUnitSpecificProductLength() {
        return BaseUnitSpecificProductLength;
    }

    /**
     * @param baseUnitSpecificProductLength the baseUnitSpecificProductLength to set
     */
    public void setBaseUnitSpecificProductLength(BigDecimal baseUnitSpecificProductLength) {
        BaseUnitSpecificProductLength = baseUnitSpecificProductLength;
    }

    /**
     * @return the baseUnitSpecificProductWidth
     */
    public BigDecimal getBaseUnitSpecificProductWidth() {
        return BaseUnitSpecificProductWidth;
    }

    /**
     * @param baseUnitSpecificProductWidth the baseUnitSpecificProductWidth to set
     */
    public void setBaseUnitSpecificProductWidth(BigDecimal baseUnitSpecificProductWidth) {
        BaseUnitSpecificProductWidth = baseUnitSpecificProductWidth;
    }

    /**
     * @return the baseUnitSpecificProductHeight
     */
    public BigDecimal getBaseUnitSpecificProductHeight() {
        return BaseUnitSpecificProductHeight;
    }

    /**
     * @param baseUnitSpecificProductHeight the baseUnitSpecificProductHeight to set
     */
    public void setBaseUnitSpecificProductHeight(BigDecimal baseUnitSpecificProductHeight) {
        BaseUnitSpecificProductHeight = baseUnitSpecificProductHeight;
    }

    public void setProductMeasurementUnit(String ProductMeasurementUnit) {
        this.ProductMeasurementUnit = ProductMeasurementUnit;
    }

    public String getProductMeasurementUnit() {
        return ProductMeasurementUnit;
    }

    public void setProductMeasurementISOUnit(String ProductMeasurementISOUnit) {
        this.ProductMeasurementISOUnit = ProductMeasurementISOUnit;
    }

    public String getProductMeasurementISOUnit() {
        return ProductMeasurementISOUnit;
    }

    public void setArticleCategory(String ArticleCategory) {
        this.ArticleCategory = ArticleCategory;
    }

    public String getArticleCategory() {
        return ArticleCategory;
    }

    public void setIndustrySector(String IndustrySector) {
        this.IndustrySector = IndustrySector;
    }

    public String getIndustrySector() {
        return IndustrySector;
    }

    /**
     * @return the creationDate
     */
    public String getCreationDate() {
        return CreationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    /**
     * @return the creationDateTime
     */
    public String getCreationDateTime() {
        return CreationDateTime;
    }

    /**
     * @param creationDateTime the creationDateTime to set
     */
    public void setCreationDateTime(String creationDateTime) {
        CreationDateTime = creationDateTime;
    }

    /**
     * @return the lastChangeDate
     */
    public String getLastChangeDate() {
        return LastChangeDate;
    }

    /**
     * @param lastChangeDate the lastChangeDate to set
     */
    public void setLastChangeDate(String lastChangeDate) {
        LastChangeDate = lastChangeDate;
    }

    /**
     * @return the lastChangeDateTime
     */
    public String getLastChangeDateTime() {
        return LastChangeDateTime;
    }

    /**
     * @param lastChangeDateTime the lastChangeDateTime to set
     */
    public void setLastChangeDateTime(String lastChangeDateTime) {
        LastChangeDateTime = lastChangeDateTime;
    }

    public void setLastChangeTime(String LastChangeTime) {
        this.LastChangeTime = LastChangeTime;
    }

    public String getLastChangeTime() {
        return LastChangeTime;
    }

    public void setDangerousGoodsIndProfile(String DangerousGoodsIndProfile) {
        this.DangerousGoodsIndProfile = DangerousGoodsIndProfile;
    }

    public String getDangerousGoodsIndProfile() {
        return DangerousGoodsIndProfile;
    }

    public void setProductDocumentChangeNumber(String ProductDocumentChangeNumber) {
        this.ProductDocumentChangeNumber = ProductDocumentChangeNumber;
    }

    public String getProductDocumentChangeNumber() {
        return ProductDocumentChangeNumber;
    }

    public void setProductDocumentPageCount(String ProductDocumentPageCount) {
        this.ProductDocumentPageCount = ProductDocumentPageCount;
    }

    public String getProductDocumentPageCount() {
        return ProductDocumentPageCount;
    }

    public void setProductDocumentPageNumber(String ProductDocumentPageNumber) {
        this.ProductDocumentPageNumber = ProductDocumentPageNumber;
    }

    public String getProductDocumentPageNumber() {
        return ProductDocumentPageNumber;
    }

    public void setDocumentIsCreatedByCAD(boolean DocumentIsCreatedByCAD) {
        this.DocumentIsCreatedByCAD = DocumentIsCreatedByCAD;
    }

    public boolean getDocumentIsCreatedByCAD() {
        return DocumentIsCreatedByCAD;
    }

    public void setProductionOrInspectionMemoTxt(String ProductionOrInspectionMemoTxt) {
        this.ProductionOrInspectionMemoTxt = ProductionOrInspectionMemoTxt;
    }

    public String getProductionOrInspectionMemoTxt() {
        return ProductionOrInspectionMemoTxt;
    }

    public void setProductionMemoPageFormat(String ProductionMemoPageFormat) {
        this.ProductionMemoPageFormat = ProductionMemoPageFormat;
    }

    public String getProductionMemoPageFormat() {
        return ProductionMemoPageFormat;
    }

    public void setProductIsHighlyViscous(boolean ProductIsHighlyViscous) {
        this.ProductIsHighlyViscous = ProductIsHighlyViscous;
    }

    public boolean getProductIsHighlyViscous() {
        return ProductIsHighlyViscous;
    }

    public void setTransportIsInBulk(boolean TransportIsInBulk) {
        this.TransportIsInBulk = TransportIsInBulk;
    }

    public boolean getTransportIsInBulk() {
        return TransportIsInBulk;
    }

    public void setProdEffctyParamValsAreAssigned(boolean ProdEffctyParamValsAreAssigned) {
        this.ProdEffctyParamValsAreAssigned = ProdEffctyParamValsAreAssigned;
    }

    public boolean getProdEffctyParamValsAreAssigned() {
        return ProdEffctyParamValsAreAssigned;
    }

    public void setProdIsEnvironmentallyRelevant(boolean ProdIsEnvironmentallyRelevant) {
        this.ProdIsEnvironmentallyRelevant = ProdIsEnvironmentallyRelevant;
    }

    public boolean getProdIsEnvironmentallyRelevant() {
        return ProdIsEnvironmentallyRelevant;
    }

    public void setLaboratoryOrDesignOffice(String LaboratoryOrDesignOffice) {
        this.LaboratoryOrDesignOffice = LaboratoryOrDesignOffice;
    }

    public String getLaboratoryOrDesignOffice() {
        return LaboratoryOrDesignOffice;
    }

    public void setPackagingProductGroup(String PackagingProductGroup) {
        this.PackagingProductGroup = PackagingProductGroup;
    }

    public String getPackagingProductGroup() {
        return PackagingProductGroup;
    }

    public void setPackingReferenceProduct(String PackingReferenceProduct) {
        this.PackingReferenceProduct = PackingReferenceProduct;
    }

    public String getPackingReferenceProduct() {
        return PackingReferenceProduct;
    }

    public void setBasicProduct(String BasicProduct) {
        this.BasicProduct = BasicProduct;
    }

    public String getBasicProduct() {
        return BasicProduct;
    }

    public void setProductDocumentNumber(String ProductDocumentNumber) {
        this.ProductDocumentNumber = ProductDocumentNumber;
    }

    public String getProductDocumentNumber() {
        return ProductDocumentNumber;
    }

    public void setProductDocumentVersion(String ProductDocumentVersion) {
        this.ProductDocumentVersion = ProductDocumentVersion;
    }

    public String getProductDocumentVersion() {
        return ProductDocumentVersion;
    }

    public void setProductDocumentType(String ProductDocumentType) {
        this.ProductDocumentType = ProductDocumentType;
    }

    public String getProductDocumentType() {
        return ProductDocumentType;
    }

    public void setProductDocumentPageFormat(String ProductDocumentPageFormat) {
        this.ProductDocumentPageFormat = ProductDocumentPageFormat;
    }

    public String getProductDocumentPageFormat() {
        return ProductDocumentPageFormat;
    }

    public void setProdChmlCmplncRelevanceCode(String ProdChmlCmplncRelevanceCode) {
        this.ProdChmlCmplncRelevanceCode = ProdChmlCmplncRelevanceCode;
    }

    public String getProdChmlCmplncRelevanceCode() {
        return ProdChmlCmplncRelevanceCode;
    }

    public void setDiscountInKindEligibility(String DiscountInKindEligibility) {
        this.DiscountInKindEligibility = DiscountInKindEligibility;
    }

    public String getDiscountInKindEligibility() {
        return DiscountInKindEligibility;
    }

    public void setProductManufacturerNumber(String ProductManufacturerNumber) {
        this.ProductManufacturerNumber = ProductManufacturerNumber;
    }

    public String getProductManufacturerNumber() {
        return ProductManufacturerNumber;
    }

    public void setManufacturerNumber(String ManufacturerNumber) {
        this.ManufacturerNumber = ManufacturerNumber;
    }

    public String getManufacturerNumber() {
        return ManufacturerNumber;
    }

    public void setManufacturerPartProfile(String ManufacturerPartProfile) {
        this.ManufacturerPartProfile = ManufacturerPartProfile;
    }

    public String getManufacturerPartProfile() {
        return ManufacturerPartProfile;
    }

    public void setOwnInventoryManagedProduct(String OwnInventoryManagedProduct) {
        this.OwnInventoryManagedProduct = OwnInventoryManagedProduct;
    }

    public String getOwnInventoryManagedProduct() {
        return OwnInventoryManagedProduct;
    }

    public void setYY1_CUSTOMERMATERIAL_PRD(String YY1_CUSTOMERMATERIAL_PRD) {
        this.YY1_CUSTOMERMATERIAL_PRD = YY1_CUSTOMERMATERIAL_PRD;
    }

    public String getYY1_CUSTOMERMATERIAL_PRD() {
        return YY1_CUSTOMERMATERIAL_PRD;
    }

    public void setSAP__Messages(List<String> SAP__Messages) {
        this.SAP__Messages = SAP__Messages;
    }

    public List<String> getSAP__Messages() {
        return SAP__Messages;
    }

    public void set_ProductDescription(List<_ProductDescription> _ProductDescription) {
        this._ProductDescription = _ProductDescription;
    }

    public List<_ProductDescription> get_ProductDescription() {
        return _ProductDescription;
    }

    public void set_ProductPlant(List<_ProductPlant> _ProductPlant) {
        this._ProductPlant = _ProductPlant;
    }

    public List<_ProductPlant> get_ProductPlant() {
        return _ProductPlant;
    }

}
