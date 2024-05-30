; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %while.begin0

while.begin0:                                     ; preds = %end07, %mainEntry
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sle i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %true0, label %false0

true0:                                            ; preds = %while.begin0
  %a2 = load i32, i32* %a, align 4
  %subtmp = sub i32 %a2, 1
  store i32 %subtmp, i32* %a, align 4
  %count3 = load i32, i32* %count, align 4
  %addtmp = add i32 %count3, 1
  store i32 %addtmp, i32* %count, align 4
  br label %while.begin04

false0:                                           ; preds = %while.begin0
  br label %end0

end0:                                             ; preds = %false0
  %count22 = load i32, i32* %count, align 4
  ret i32 %count22

while.begin04:                                    ; preds = %end016, %true0
  %count8 = load i32, i32* %count, align 4
  %cmptmp9 = icmp slt i32 %count8, 100
  %zexttmp10 = zext i1 %cmptmp9 to i32
  %finalcmp11 = icmp ne i32 0, %zexttmp10
  br i1 %finalcmp11, label %true05, label %false06

true05:                                           ; preds = %while.begin04
  %count12 = load i32, i32* %count, align 4
  %addtmp13 = add i32 %count12, 30
  store i32 %addtmp13, i32* %count, align 4
  %count17 = load i32, i32* %count, align 4
  %cmptmp18 = icmp sgt i32 %count17, 90
  %zexttmp19 = zext i1 %cmptmp18 to i32
  %finalcmp20 = icmp ne i32 0, %zexttmp19
  br i1 %finalcmp20, label %true014, label %false015

false06:                                          ; preds = %while.begin04
  br label %end07

end07:                                            ; preds = %false06
  br label %while.begin0

true014:                                          ; preds = %true05
  %count21 = load i32, i32* %count, align 4
  ret i32 %count21
  br label %end016

false015:                                         ; preds = %true05
  br label %end016

end016:                                           ; preds = %false015, %true014
  br label %while.begin04
}
