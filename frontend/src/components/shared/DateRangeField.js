
export const DateRangeField = ({ label, startValue, endValue, onStartChange, onEndChange, disabled }) => (
    <>
      <label>{label}</label>
      <div className="date-range">
        <input
          type="date"
          value={startValue}
          onChange={onStartChange}
          disabled={disabled}
          aria-required="true"
        />
        to
        <input
          type="date"
          value={endValue}
          onChange={onEndChange}
          disabled={disabled}
          aria-required="true"
        />
      </div>
    </>
  );