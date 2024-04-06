
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

class Subscription {
	public Subscription() {
	}

	public Subscription(int id, int customerId, int monthlyPriceInCents) {
		this.id = id;
		this.customerId = customerId;
		this.monthlyPriceInCents = monthlyPriceInCents;
	}

	public int id;
	public int customerId;
	public int monthlyPriceInCents;
}

class User {
	public User() {
	}

	public User(int id, String name, LocalDate activatedOn, LocalDate deactivatedOn, int customerId) {
		this.id = id;
		this.name = name;
		this.activatedOn = activatedOn;
		this.deactivatedOn = deactivatedOn;
		this.customerId = customerId;
	}

	public int id;
	public String name;
	public LocalDate activatedOn;
	public LocalDate deactivatedOn;
	public int customerId;
}

class Challenge {
	/// Computes the monthly charge for a given subscription.
	///
	/// @returns The total monthly bill for the customer in cents, rounded
	/// to the nearest cent. For example, a bill of $20.00 should return 2000.
	/// If there are no active users or the subscription is null, returns 0.
	///
	/// @param month - Always present
	/// Has the following structure:
	/// "2022-04" // April 2022 in YYYY-MM format
	///
	/// @param subscription - May be null
	/// If present, has the following structure (see Subscription class):
	/// {
	/// Id: 763,
	/// CustomerId: 328,
	/// MonthlyPriceInCents: 359 // price per active user per month
	/// }
	///
	/// @param users - May be empty, but not null
	/// Has the following structure (see User class):
	/// [
	/// {
	/// id: 1,
	/// name: "Employee #1",
	/// customerId: 1,
	///
	/// // when this user started
	/// activatedOn: new Date("2021-11-04"),
	///
	/// // last day to bill for user
	/// // should bill up to and including this date
	/// // since user had some access on this date
	/// deactivatedOn: new Date("2022-04-10")
	/// },
	/// {
	/// id: 2,
	/// name: "Employee #2",
	/// customerId: 1,
	///
	/// // when this user started
	/// activatedOn: new Date("2021-12-04"),
	///
	/// // hasn't been deactivated yet
	/// deactivatedOn: null
	/// },
	/// ]
	// TODO: We are not handling the case of partial month billing, when we are
	/// calling this procedure in the middle of the current month,
	// in this case we are returning the PROJECTED full charge for this month
	public static int monthlyCharge(String date, Subscription subscription, User[] users) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
		YearMonth billingYearMonth = YearMonth.parse(date, formatter);

		// Calculate number of days the client users had an active subscription in this
		// month
		int totalDaysPerCustomer = 0;
		for (User user : users) {

			if (user.activatedOn.isAfter(billingYearMonth.atEndOfMonth())) {
				// If user subscription started after the end of the month, no charge for this
				// month
				continue;
			} else if (user.deactivatedOn != null && user.deactivatedOn.isBefore(billingYearMonth.atDay(1))) {
				// Subscription deactivated before the billing month, no charge
				continue; 
			} else if (user.deactivatedOn == null || user.deactivatedOn.isAfter(billingYearMonth.atEndOfMonth())) {
				// If user subscription is still active
				
				if (user.activatedOn.isBefore(billingYearMonth.atDay(1))) {
					// If user subscription started before this month, charge for the full month
					totalDaysPerCustomer += billingYearMonth.lengthOfMonth();
				} else {
					// Otherwise the subscription started sometime this month and we charge for the
					// partial month from that date till the end of the month including the activation day
					totalDaysPerCustomer += billingYearMonth.lengthOfMonth() - user.activatedOn.getDayOfMonth() + 1;
				}
			} else {
				// Subscription deactivated sometime this month 

				if (user.activatedOn.isBefore(billingYearMonth.atDay(1))) {
					// If user subscription started before this month and we charge for the partial
					// month from the start of the month till the deactivation date
					totalDaysPerCustomer += user.deactivatedOn.getDayOfMonth();
				} else {
					// User subscription started and ended this month and we charge for the partial
					// month including activation and deactivation days
					totalDaysPerCustomer += user.deactivatedOn.getDayOfMonth() - user.activatedOn.getDayOfMonth() + 2;
				}
			}
		}

		// Now we can calculate the billing charge for all customer users this month,
		// rounding to the closest cent
		return Math.round(subscription.monthlyPriceInCents * totalDaysPerCustomer / billingYearMonth.lengthOfMonth());
	}
}