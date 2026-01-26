ALTER TABLE public.contributions
  ADD COLUMN platform_commission_reserved numeric(38,2) NOT NULL DEFAULT 0,
  ADD COLUMN operator_fee numeric(38,2),
  ADD COLUMN operator_fee_status varchar(20) NOT NULL DEFAULT 'PENDING',
  ADD COLUMN operator_fee_settled_at timestamp without time zone,

  ADD COLUMN net_to_target numeric(38,2) GENERATED ALWAYS AS
    (value - platform_commission_reserved) STORED,

  ADD COLUMN platform_profit numeric(38,2) GENERATED ALWAYS AS
    (platform_commission_reserved - COALESCE(operator_fee, 0)) STORED;

ALTER TABLE public.contributions
  ADD CONSTRAINT contributions_value_nonneg CHECK (value >= 0),
  ADD CONSTRAINT contributions_reserved_nonneg CHECK (platform_commission_reserved >= 0),
  ADD CONSTRAINT contributions_reserved_le_value CHECK (platform_commission_reserved <= value),
  ADD CONSTRAINT contributions_operator_fee_nonneg CHECK (operator_fee IS NULL OR operator_fee >= 0),
  ADD CONSTRAINT contributions_operator_fee_le_reserved CHECK
    (operator_fee IS NULL OR operator_fee <= platform_commission_reserved);
