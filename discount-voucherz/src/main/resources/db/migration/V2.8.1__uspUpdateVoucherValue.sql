IF OBJECT_ID('uspUpdateVoucherValue') IS NULL
  EXEC('CREATE PROCEDURE [dbo].[uspUpdateVoucherValue] AS SET NOCOUNT ON')
GO
ALTER PROCEDURE [dbo].[uspUpdateVoucherValue]
  @merchantId nvarchar (62),
  @userId nvarchar (62),
  @code nvarchar (62),
  @value decimal(18,2)
AS
BEGIN
  BEGIN TRY
    BEGIN TRANSACTION
      UPDATE [dbo].[DiscountVoucher]
      SET [value] = @value
      WHERE userId = @userId
      AND merchantId = @merchantId
      AND code = @code
      AND isDeleted = 0
      AND (expiryDate >= getdate() OR expiryDate IS NULL)
    COMMIT
      IF @@ROWCOUNT = 1
        RETURN;
      ELSE
        RAISERROR('DiscountVoucher Updates Failed.', 16, 1);
  END TRY
  BEGIN CATCH
    IF @@TRANCOUNT > 0
    ROLLBACK;
    EXECUTE [dbo].[uspLogError];
  END CATCH
END