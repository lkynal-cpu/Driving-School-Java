# Harris Driving School Management System (Group G13)

Menu-driven Java command-line system (no GUI, no database). Data is saved in text files in the `data/` folder.
Packages: `ug.ac.vu.g13.core`, `.students`, `.instructors`, `.vehicles`, `.courses`, `.lessons`, `.fees`, `.tests`, `.maintenance`, `.app`.

## Run
Needs JDK 11 or newer (tested on 21).

    Linux / Mac:  ./run.sh
    Windows:      run.bat

Or by hand:

    javac -d out $(find src -name "*.java")
    java -cp out ug.ac.vu.g13.app.Main

## Modules and owners
Replace the `@author Member N` tags with your real names before submitting.

| # | Module (package)     | Owner (suggested)        | Model classes                                  | Custom exception               | Service classes                    |
|---|----------------------|--------------------------|------------------------------------------------|--------------------------------|------------------------------------|
| - | core (shared)        | Kefas, Integration lead  | BaseRecord (abstract), Reportable, Payable (interfaces), InputHelper, BaseService | -                              | BaseService<T>                     |
| 1 | students             | Kefas                    | Student, PermitApplication                     | DuplicateStudentException      | StudentService, PermitService      |
| 2 | instructors          | Member 2                 | Instructor, Shift                              | InstructorUnavailableException | InstructorService, ShiftService    |
| 3 | vehicles             | Member 3                 | Vehicle (abstract), Car, Motorcycle, Truck     | VehicleNotAvailableException   | VehicleService                     |
| 4 | courses              | Member 4                 | Course, Enrollment                             | CourseFullException            | CourseService, EnrollmentService   |
| 5 | lessons              | Member 5                 | Lesson (abstract), PracticalLesson, TheoryLesson | SchedulingConflictException  | LessonService                      |
| 6 | fees                 | Member 6                 | Invoice (Payable), Payment                     | OverpaymentException           | InvoiceService, PaymentService     |
| 7 | tests                | Nakito Chucuna Joyce                | TestBooking, TestResult                        | NotEligibleForTestException    | TestBookingService, TestResultService |
| 8 | maintenance          | Member 8                 | MaintenanceJob, FuelLog                        | JobAlreadyClosedException      | MaintenanceService, FuelService    |

## Record IDs (all start with the group number)
S student, A permit application, I instructor, H shift, V vehicle, C course, E enrolment, L lesson,
N invoice, P payment, T test booking, R test result, M maintenance job, F fuel log. Example: `G13-S001`, `G13-P014`.

## Menu tree
    MAIN MENU
    1 Students: register, list, search, update, remove, report (sort by name/registration/age), add permit application, change permit status, list permits, overview
    2 Instructors: register, list, find, update, remove, report (by experience / by class / active), add shift, list shifts, remove shift, overview
    3 Vehicles: add, list, find, update, remove, report (newest / mileage / class / status), fleet overview
    4 Courses and Enrolment: add, list, update, remove course; enrol, list, change status; report (cheapest / by course / status / seats left); overview
    5 Lessons: schedule practical, schedule theory, list, complete, cancel, edit notes, remove, report (upcoming / student / instructor / status / hours taught), overview
    6 Fees and Payments: issue invoice, list, change due date, remove; record payment, list, reverse; report (unpaid / overdue / by amount / by method / totals); overview
    7 Driving Tests: book, list, cancel, remove; record result, correct score, list results; report (by score / passed / failed / pass rate); overview
    8 Maintenance and Fuel: open job, mark in progress, close job, list, edit, remove; log fuel, list, remove; report (by cost / open jobs / running cost per vehicle); overview
    0 Exit

## How the modules work together (object references)
- Enrollment -> Student + Course
- Invoice -> Enrollment (amount due = course fee); Payment -> Invoice
- Lesson -> Student + Instructor; PracticalLesson -> Vehicle. Scheduling checks: active enrolment for that vehicle class, instructor qualified and on shift, vehicle AVAILABLE, no clash for instructor / student / vehicle.
- TestBooking -> Student. Booking rules: active course for the class, at least 50% of fees paid, theory passed before practical, no repeat of a test already passed.
- MaintenanceJob / FuelLog -> Vehicle. Opening a job sets the vehicle to IN_MAINTENANCE; closing the last open job sets it back to AVAILABLE. Fuel logs update the odometer.
- Removal guards (wired in `Main`) stop you deleting a record another module still uses (for example a student with enrolments).

## Requirement checklist (brief, Step 5)
- Abstract class + interface used by every module: `BaseRecord`, `Reportable` (and `Payable` in fees)
- One `InputHelper` for all input; invalid input re-asks, end of input exits cleanly
- One main menu leading to every module submenu: `app/Main.java`
- Private fields, validating constructors and setters that throw `InvalidDataException`
- Polymorphic loop in every module (the `overview` option)
- Service with ArrayList + add / find / update / remove: all extend `BaseService<T>`
- Comparator-based sorted or filtered report in every module
- File storage: loads at start-up, saves after every change, skips damaged lines, copes with missing files, writes through a temp file
- `@author` tag at the top of every class

## Notes
- Phone numbers must look like `0700000000` or `+256700000000`. Use invented numbers only (the brief forbids real ones).
- Change request (Checkpoint 2): rules you can change in one place, for example `TestBookingService.MIN_FEES_PAID`, `Student.MIN_AGE`, `TestResult.THEORY_PASS_MARK`.
- The UML class diagram and test plan are still yours to draw and write; they must match this code.
