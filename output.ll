; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  %a1 = load i32, i32* %a, align 4
  %addtmp = add i32 %a1, 1
  %cmptmp = icmp ne i32 %addtmp, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %true0, label %false0

true0:                                            ; preds = %mainEntry
  %a2 = load i32, i32* %a, align 4
  ret i32 %a2
  br label %end0

false0:                                           ; preds = %mainEntry
  br label %end0

end0:                                             ; preds = %false0, %true0
  %count3 = load i32, i32* %count, align 4
  ret i32 %count3
}
