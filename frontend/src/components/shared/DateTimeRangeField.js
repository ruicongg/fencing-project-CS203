
export const DateTimeRangeField = ({ label, startValue, endValue, onStartChange, onEndChange, disabled }) => (
    <>
      <label>{label}</label>
      <div className="date-range">
        <input
          type="datetime-local"
          value={startValue}
          onChange={onStartChange}
          disabled={disabled}
          aria-required="true"
        />
        to
        <input
          type="datetime-local"
          value={endValue}
          onChange={onEndChange}
          disabled={disabled}
          aria-required="true"
        />
      </div>
    </>
  );