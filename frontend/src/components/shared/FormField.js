export const FormField = ({
  id,
  type = "text",
  value,
  onChange,
  disabled,
  placeholder,
}) => (
  <>
    <input
      id={id}
      type={type}
      value={value}
      onChange={onChange}
      disabled={disabled}
      placeholder={placeholder}
      aria-required="true"
    />
  </>
);
